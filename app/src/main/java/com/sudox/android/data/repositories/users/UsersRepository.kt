package com.sudox.android.data.repositories.users

import com.sudox.android.common.helpers.objects.MutablePair
import com.sudox.android.data.database.dao.user.UserDao
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.models.users.dto.UsersInfoDTO
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.NetworkException
import kotlinx.coroutines.*
import java.util.concurrent.Semaphore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepository @Inject constructor(private val authRepository: AuthRepository,
                                          private val accountRepository: AccountRepository,
                                          private val protocolClient: ProtocolClient,
                                          private val userDao: UserDao) {

    @JvmField
    var loadedUsersIds: HashSet<Long> = HashSet()
    val usersLoadingLock = Semaphore(1)
    val usersUpdatesCallbacks: ArrayList<Pair<Long, MutablePair<(User) -> (Unit), Int>>> = ArrayList()

    init {
        listenSessionStatus()
    }

    private fun listenSessionStatus() = GlobalScope.launch(Dispatchers.IO) {
        for (state in authRepository
                .accountSessionStateChannel
                .openSubscription()) {

            if (!state) {
                usersUpdatesCallbacks.clear()
                usersLoadingLock.release()
                loadedUsersIds.clear()
            } else {
                val account = accountRepository.getAccount() ?: return@launch
                val accountId = accountRepository.getAccountId(account) ?: return@launch

                // Remove all ids except id of current user
                loadedUsersIds.removeAll { it != accountId }

                // Check updates ...
                loadUsers(usersUpdatesCallbacks.map { it.first }.distinct())
            }
        }
    }

    fun loadUsers(ids: List<Long>,
                  onlyFromNetwork: Boolean = false,
                  onlyFromDatabase: Boolean = false) = GlobalScope.async(Dispatchers.IO) {

        // Optimization: no needed block the semaphore in some situations
        if (ids.isEmpty() || (onlyFromNetwork && !authRepository.canExecuteNetworkRequest())) {
            return@async arrayListOf<User>()
        }

        // Block requests queue ...
        usersLoadingLock.acquire()

        val result = if (onlyFromNetwork && authRepository.canExecuteNetworkRequest()) {
            fetchUsers(ids, loadFromDatabaseIfConnectionDropped = false)
        } else if (onlyFromDatabase || !authRepository.canExecuteNetworkRequest()) {
            ArrayList(userDao.loadByIds(ids))
        } else if (authRepository.canExecuteNetworkRequest()) {
            val storedIdsInDatabase = ids.filter { loadedUsersIds.contains(it) }

            // Optimization: No need filter ids for network loading if all requested ids stored in database
            if (storedIdsInDatabase.size == ids.size) {
                ArrayList(userDao.loadByIds(ids))
            } else {
                val networkLoadingIds = ids.filter { !storedIdsInDatabase.contains(it) }
                val fetchedUsers = fetchUsers(networkLoadingIds)

                // Optimization: No need load from database if all requested ids not loaded
                if (networkLoadingIds.size == ids.size) {
                    fetchedUsers
                } else {
                    fetchedUsers.plus(userDao.loadByIds(storedIdsInDatabase))
                }
            }
        } else {
            ArrayList()
        }

        // Unblock
        usersLoadingLock.release()

        // Return as result
        return@async result as ArrayList<User>
    }

    fun loadUser(id: Long,
                 onlyFromDatabase: Boolean = false,
                 onlyFromNetwork: Boolean = false) = GlobalScope.async(Dispatchers.IO) {

        return@async loadUsers(listOf(id), onlyFromNetwork, onlyFromDatabase)
                .await()
                .firstOrNull()
    }

    internal suspend fun fetchUsers(ids: List<Long>, loadFromDatabaseIfConnectionDropped: Boolean = true): ArrayList<User> {
        try {
            val usersInfoDTO = authRepository.makeRequestWithSession<UsersInfoDTO>(protocolClient, "users.get", UsersInfoDTO().apply {
                this.ids = ids
            }, notifyToEventBus = false).await()

            if (usersInfoDTO.isSuccess()) {
                val fetchedIds = usersInfoDTO.users!!.map { it.id }
                val contactsIds = userDao.filterContactsIds(fetchedIds)
                val saveableUsers = usersInfoDTO
                        .users!!
                        .map { User(it.id, it.name, it.nickname, it.photo, it.phone, it.status, it.bio, contactsIds.contains(it.id)) }

                // Cache users ..
                saveOrUpdateUsers(*saveableUsers.toTypedArray()).await()
                loadedUsersIds.plusAssign(saveableUsers.map { it.uid })

                // Optimization: no need to removing invalid users if fetched users not not less than requested
                if (ids.size > fetchedIds.size) {
                    val notExistsIds = ids.filter { !fetchedIds.contains(it) }.toLongArray()

                    // Remove invalid users from database
                    removeUsers(*notExistsIds).await()
                }

                // As result ...
                return ArrayList(saveableUsers)
            } else if (usersInfoDTO.error == Errors.INVALID_USERS) {
                removeUsers(*ids.toLongArray()).await()
            }
        } catch (e: NetworkException) {
            if (loadFromDatabaseIfConnectionDropped) {
                return ArrayList(userDao.loadByIds(ids))
            }
        }

        return arrayListOf()
    }

    fun removeUsers(vararg ids: Long, updateExists: Boolean = false) = GlobalScope.async(Dispatchers.IO) {
        val existsIds = if (updateExists) userDao.filterExists(ids) else ids.toList()

        loadedUsersIds.minusAssign(existsIds)
        userDao.removeAll(existsIds)

        if (updateExists) {
            // saveOrUpdateUsers and notifyUsersUpdated methods will be called after loadUsers cache update ...
            return@async loadUsers(existsIds, onlyFromNetwork = true).await()
        } else {
            // No needed to listen updates but users was removed ...
            unsubscribeFromUsersUpdates(*ids)

            // Nothing to return ...
            return@async null
        }
    }

    fun saveOrUpdateUsers(vararg users: User) = GlobalScope.async(Dispatchers.IO) {
        userDao.insertAll(users)
        notifyUsersUpdated(*users)
    }

    fun subscribeToUserUpdates(id: Long, callback: (User) -> (Unit)) {
        usersUpdatesCallbacks.plusAssign(Pair(id, MutablePair(callback, 0)))
    }

    fun unsubscribeFromUserUpdates(callback: ((User) -> (Unit))?) {
        usersUpdatesCallbacks
                .filter { (if (callback != null) it.second.first == callback else true) }
                .forEach { this@UsersRepository.usersUpdatesCallbacks.remove(it) }
    }

    fun unsubscribeFromUsersUpdates(vararg ids: Long) {
        usersUpdatesCallbacks
                .filter { ids.contains(it.first) }
                .forEach { this@UsersRepository.usersUpdatesCallbacks.remove(it) }
    }

    fun notifyUsersUpdated(vararg users: User) {
        users.forEach { user ->
            val newHash = user.hashCode()

            // Optimization: update users only if hashes different ...
            usersUpdatesCallbacks
                    .filter { it.first == user.uid && it.second.second != newHash }
                    .forEach { pair ->
                        pair.second.second = newHash
                        pair.second.first.invoke(user)
                    }
        }
    }
}