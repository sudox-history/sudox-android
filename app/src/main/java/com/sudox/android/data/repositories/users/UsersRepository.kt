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

    /**
     * Начинает прослушку состояния сессии.
     *
     * Если сессия валидна, то происходит импорт токена.
     * Если сессия невалидна, то происходит отключение всех кэллбеков.
     *
     * В обоих случаях будет очищен кэш и снята блокировка очереди.
     */
    private fun listenSessionStatus() = GlobalScope.launch(Dispatchers.IO) {
        for (state in authRepository.accountSessionStateChannel.openSubscription()) {
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

    /**
     * Находит правильную политику загрузки пользователей и загружает их в её соответствии.
     *
     * @param ids - ID пользователей
     * @param onlyFromNetwork - грузить ли пользователей только из сети?
     * @param onlyFromDatabase - грузить ли пользователей только из БД?
     */
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

    /**
     * Находит правильную политику загрузки и загружает пользователя в её соответствии.
     *
     * @param id - ID пользователя
     * @param onlyFromNetwork - грузить ли пользователей только из сети?
     * @param onlyFromDatabase - грузить ли пользователей только из БД?
     */
    fun loadUser(id: Long,
                 onlyFromDatabase: Boolean = false,
                 onlyFromNetwork: Boolean = false) = GlobalScope.async(Dispatchers.IO) {

        return@async loadUsers(listOf(id), onlyFromNetwork, onlyFromDatabase)
                .await()
                .firstOrNull()
    }

    /**
     * Загружает актуальные данные об пользователях.
     * Экономит трафик за счет ожидания и переиспользования подобных запросов.
     *
     * @param ids - ID пользователей
     */
    fun loadActualUsers(ids: List<Long>) = GlobalScope.async(Dispatchers.IO) {
        if (ids.isEmpty() || !authRepository.canExecuteNetworkRequest()) {
            return@async arrayListOf<User>()
        }

        loadedUsersIds.removeAll(ids)
        usersLoadingLock.acquire()

        // User could be loaded during the lock.
        val storedUsersId = ids.filter { loadedUsersIds.contains(it) }

        // Optimization: all actual users stored in database
        val result = if (storedUsersId.size == ids.size) {
            ArrayList(userDao.loadByIds(storedUsersId))
        } else {
            val notLoadedUsersIds = ids.filter { !storedUsersId.contains(it) }
            val networkUsers = fetchUsers(notLoadedUsersIds, loadFromDatabaseIfConnectionDropped = false)

            // Optimization: All ids loaded from network ...
            if (notLoadedUsersIds.size == ids.size) {
                networkUsers
            } else {
                networkUsers.plus(userDao.loadByIds(notLoadedUsersIds))
            }
        }

        // Free queue ...
        usersLoadingLock.release()

        // Merge results
        return@async result as ArrayList<User>
    }

    /**
     * Загружает актуальные данные об пользователе.
     * Экономит трафик за счет ожидания и переиспользования подобных запросов.
     *
     * @param id - ID пользователя
     */
    fun loadActualUser(id: Long) = GlobalScope.async(Dispatchers.IO) {
        return@async loadActualUsers(listOf(id))
                .await()
                .firstOrNull()
    }

    /**
     * Загружает пользователя из сети и кеширует их.
     *
     * @param ids - ID пользователей
     * @param loadFromDatabaseIfConnectionDropped - загружать с БД если возникнет ошибка, связанная с подключением к серверу
     */
    fun fetchUsers(ids: List<Long>, loadFromDatabaseIfConnectionDropped: Boolean = true): ArrayList<User> = runBlocking<ArrayList<User>> {
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
                return@runBlocking ArrayList(saveableUsers)
            } else if (usersInfoDTO.error == Errors.INVALID_USERS) {
                removeUsers(*ids.toLongArray()).await()
            }
        } catch (e: NetworkException) {
            if (loadFromDatabaseIfConnectionDropped) {
                return@runBlocking ArrayList(userDao.loadByIds(ids))
            }
        }

        return@runBlocking arrayListOf()
    }

    /**
     * Удаляет пользователя из кэша и обновляет если требуется.
     *
     * @param ids - ID пользователей
     * @param updateExists - требуется ли обновлять пользователя после удаления из кэша?
     */
    fun removeUsers(vararg ids: Long, updateExists: Boolean = false, unsubscribeFromUpdates: Boolean = true) = GlobalScope.async(Dispatchers.IO) {
        val existsIds = if (updateExists) userDao.filterExists(ids) else ids.toList()

        // Remove old users data ...
        userDao.removeAll(existsIds)
        loadedUsersIds.minusAssign(existsIds)

        if (updateExists) {
            // saveOrUpdateUsers and notifyUsersUpdated methods will be called after loadUsers cache update ...
            return@async loadActualUsers(existsIds).await()
        } else if (unsubscribeFromUpdates) {
            unsubscribeFromUsersUpdates(*ids)
        }

        // Nothing to return ...
        return@async null
    }

    /**
     * Сохраняет/обновляет пользователей в кэше, уведомляет об этом их слушателей
     *
     * @param users - обьекты пользователей
     */
    fun saveOrUpdateUsers(vararg users: User) = GlobalScope.async(Dispatchers.IO) {
        userDao.insertAll(users)
        notifyUsersUpdated(*users)
    }

    /**
     * Подписывает пользователя с указанным ID на нотификации в случае обновления
     *
     * @param id - ID пользователя
     * @param callback - функция, которая будет вызвана в случае обновления
     */
    fun subscribeToUserUpdates(id: Long, callback: (User) -> (Unit)) {
        usersUpdatesCallbacks.plusAssign(Pair(id, MutablePair(callback, 0)))
    }

    /**
     * Отписывает указанную функцию от нотификации в случае обновлений
     *
     * @param callback - функция, которая будет вызвана в случае обновления
     */
    fun unsubscribeFromUserUpdates(callback: ((User) -> (Unit))?) {
        usersUpdatesCallbacks
                .filter { (if (callback != null) it.second.first == callback else true) }
                .forEach { this@UsersRepository.usersUpdatesCallbacks.remove(it) }
    }

    /**
     * Отписывает пользователей с указанными ID от нотификаций в случае обновлений
     *
     * @param ids - ID пользователей, которых следует отписать
     */
    fun unsubscribeFromUsersUpdates(vararg ids: Long) {
        usersUpdatesCallbacks
                .filter { ids.contains(it.first) }
                .forEach { this@UsersRepository.usersUpdatesCallbacks.remove(it) }
    }

    /**
     * Уведомляет об обновлении пользователей их слушателей
     *
     * @param users - обьекты пользователей
     */
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