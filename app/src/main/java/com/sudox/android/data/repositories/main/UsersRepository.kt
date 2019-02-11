package com.sudox.android.data.repositories.main

import com.sudox.android.common.helpers.clear
import com.sudox.android.data.database.dao.user.UserDao
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.models.users.UserType
import com.sudox.android.data.models.users.dto.UserInfoDTO
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import java.util.concurrent.Semaphore
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class UsersRepository @Inject constructor(private val authRepository: AuthRepository,
                                          private val accountRepository: AccountRepository,
                                          private val protocolClient: ProtocolClient,
                                          private val userDao: UserDao) {

    // ID загруженных пользователей (для экономии трафика)
    private val loadedUsersIds: HashSet<Long> = HashSet()
    private val usersLoadingLock = Semaphore(1)

    // Текущий пользователь
    var currentUserChannel: ConflatedBroadcastChannel<User> = ConflatedBroadcastChannel()
    var listenCurrentUserUpdates: Boolean = false

    init {
        listenSessionStatus()
    }

    private fun listenSessionStatus() = GlobalScope.launch(Dispatchers.IO) {
        for (state in authRepository
                .accountSessionStateChannel
                .openSubscription()) {

            // Session is valid
            if (state) {
                loadedUsersIds.clear()

                // Update current user ..
                if (listenCurrentUserUpdates) loadCurrentUser()
            } else {
                loadedUsersIds.clear()
                currentUserChannel.clear()
                listenCurrentUserUpdates = false
                usersLoadingLock.release()
            }
        }
    }

    fun loadUsers(ids: List<Long>, loadAs: UserType = UserType.UNKNOWN, onlyFromNetwork: Boolean = false, onlyFromDatabase: Boolean = false) = GlobalScope.async(Dispatchers.IO) {
        usersLoadingLock.acquire() // Add to queue

        if (ids.isEmpty()) {
            val result = arrayListOf<User>()

            // Free queue
            usersLoadingLock.release()
            return@async result
        } else if (onlyFromNetwork && !onlyFromDatabase) {
            val result = loadUsersFromNetwork(ids, loadAs)

            // Free queue
            usersLoadingLock.release()
            return@async result
        } else if (protocolClient.isValid() && authRepository.sessionIsValid && !onlyFromDatabase) {
            val notLoadedUsers = ids.filter { !loadedUsersIds.contains(it) }
            val usersFromNetwork = if (notLoadedUsers.isNotEmpty()) loadUsersFromNetwork(notLoadedUsers, loadAs) else arrayListOf()
            val usersFromNetworkIds = usersFromNetwork.map { it.uid }
            val usersFromDatabaseIds: List<Long> = ids
                    .filter { !usersFromNetworkIds.contains(it) }
                    .map { it }
            val usersFromDatabase = userDao.loadByIds(usersFromDatabaseIds)

            // Free queue
            usersLoadingLock.release()

            // Update the type
            usersFromDatabase.forEach {
                it.type = if (it.type != UserType.UNKNOWN) it.type else loadAs
            }

            // Combine network & database founded contacts
            return@async usersFromNetwork.plus(usersFromDatabase)
        } else {
            val users = userDao.loadByIds(ids)

            // Free queue
            usersLoadingLock.release()

            // Update the type
            users.forEach {
                it.type = if (it.type != UserType.UNKNOWN) it.type else loadAs
            }

            return@async users
        }
    }

    fun loadUser(id: Long, loadAs: UserType = UserType.UNKNOWN, onlyFromDatabase: Boolean = false, onlyFromNetwork: Boolean = false) = GlobalScope.async(Dispatchers.IO) {
        usersLoadingLock.acquire()

        if ((onlyFromDatabase || !protocolClient.isValid() || loadedUsersIds.contains(id)) && !onlyFromNetwork) {
            val user = userDao.loadById(id)

            // Free queue
            usersLoadingLock.release()

            // Update the type
            if (user != null && user.type == UserType.UNKNOWN) {
                user.type = loadAs
            }

            return@async user
        } else {
            val result = loadUsersFromNetwork(listOf(id), loadAs)
                    .firstOrNull()

            // Free queue
            usersLoadingLock.release()
            return@async result
        }
    }

    fun removeUser(id: Long) = GlobalScope.launch(Dispatchers.IO) {
        loadedUsersIds.remove(id)
        userDao.removeOne(id)
    }

    fun saveOrUpdateUser(user: User) = GlobalScope.async(Dispatchers.IO) {
        userDao.insertOne(user)
    }

    private suspend fun loadUsersFromNetwork(ids: List<Long>, loadAs: UserType = UserType.UNKNOWN): List<User> = suspendCoroutine { continuation ->
        if (!authRepository.sessionIsValid) {
            usersLoadingLock.release()
            continuation.resume(arrayListOf())
            return@suspendCoroutine
        }

        protocolClient.makeRequest<UserInfoDTO>("users.get", UserInfoDTO().apply {
            this.ids = ids
        }) {
            if (it.users != null && !it.users!!.isEmpty() && !(it.containsError())) {
                val users = toStorableUsers(it, loadAs)

                // Cache users, mark as loaded
                userDao.insertAll(users)
                loadedUsersIds.plusAssign(users.map { it.uid })

                // Return result
                continuation.resume(users)
            } else {
                // Invalidate ...
                if (it.error == Errors.INVALID_USERS) userDao.removeAll(ids)

                // Return empty result, because all users with this ids was removed
                continuation.resume(arrayListOf())
            }
        }
    }

    private fun toStorableUsers(userInfoDTO: UserInfoDTO, loadAs: UserType = UserType.UNKNOWN): List<User> {
        val usersIds = userInfoDTO.users!!.map { it.id }
        val storedUsers = userDao.loadByIds(usersIds)

        return userInfoDTO.users!!.map { dto ->
            val storedUser = storedUsers.find { it.uid == dto.id }
            val type = if (storedUser != null && storedUser.type != UserType.UNKNOWN) storedUser.type else loadAs

            // Convert to user ...
            toStorableUser(dto, type, false)
        }
    }

    private fun toStorableUser(userInfoDTO: UserInfoDTO, loadAs: UserType = UserType.UNKNOWN, checkType: Boolean = true): User {
        val type: UserType = if (checkType) {
            val storedUser = userDao.loadById(userInfoDTO.id)

            // Check ...
            if (storedUser != null && storedUser.type != UserType.UNKNOWN) storedUser.type else loadAs
        } else loadAs

        return User(userInfoDTO.id,
                userInfoDTO.name,
                userInfoDTO.nickname,
                userInfoDTO.photo,
                userInfoDTO.phone,
                userInfoDTO.status,
                userInfoDTO.bio,
                type)
    }

    fun loadCurrentUser(listenFutureUpdates: Boolean = false) = GlobalScope.async(Dispatchers.IO) {
        if (accountRepository.cachedAccount != null) {
            // Listen updates ...
            if (!listenCurrentUserUpdates) {
                listenCurrentUserUpdates = listenFutureUpdates
            }

            val user = loadUser(accountRepository.cachedAccount!!.id).await()

            if (user != null) {
                currentUserChannel.offer(user)
            } else {
                currentUserChannel.clear()
            }

            return@async user
        } else {
            currentUserChannel.clear()
            return@async null
        }
    }
}