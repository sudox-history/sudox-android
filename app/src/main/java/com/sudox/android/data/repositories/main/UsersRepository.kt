package com.sudox.android.data.repositories.main

import com.sudox.android.data.database.dao.user.UserDao
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.models.users.UserType
import com.sudox.android.data.models.users.dto.UserInfoDTO
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import kotlinx.coroutines.*
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

    init {
        listenConnectionStatus()
    }

    private fun listenConnectionStatus() = GlobalScope.launch(Dispatchers.IO) {
        for (state in authRepository
                .accountSessionStateChannel
                .openSubscription()) {

            // Session is valid
            if (state) loadedUsersIds.clear()
        }
    }

    fun loadUsers(ids: List<Long>, loadAs: UserType = UserType.UNKNOWN) = GlobalScope.async(Dispatchers.IO) {
        if (ids.isEmpty()) {
            return@async arrayListOf<User>()
        } else if (ids.size == 1) {
            return@async arrayListOf(loadUser(ids[0], loadAs).await())
        } else if (protocolClient.isValid() && authRepository.sessionIsValid) {
            val notLoadedUsers = ids.filter { !loadedUsersIds.contains(it) }
            val usersFromNetwork = if (notLoadedUsers.isNotEmpty()) loadUsersFromNetwork(notLoadedUsers, loadAs) else arrayListOf()
            val usersFromNetworkIds = usersFromNetwork.map { it.uid }
            val usersFromDatabase = userDao.loadByIds(ids
                    .filter { !usersFromNetworkIds.contains(it) }
                    .map { it })

            // Update the type
            usersFromDatabase.forEach {
                it.type = if (it.type != UserType.UNKNOWN) it.type else loadAs
            }

            // Combine network & database founded contacts
            return@async usersFromNetwork.plus(usersFromDatabase)
        } else {
            val users = userDao.loadByIds(ids)

            // Update the type
            users.forEach {
                it.type = if (it.type != UserType.UNKNOWN) it.type else loadAs
            }

            return@async users
        }
    }

    fun loadUser(id: Long, loadAs: UserType = UserType.UNKNOWN, onlyFromDatabase: Boolean = false) = GlobalScope.async(Dispatchers.IO) {
        if (onlyFromDatabase || !protocolClient.isValid() || loadedUsersIds.contains(id)) {
            val user = userDao.loadById(id)

            // Update the type
            if (user != null && user.type == UserType.UNKNOWN) {
                user.type = loadAs
            }

            return@async user
        } else {
            return@async loadUserFromNetwork(id, loadAs)
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

    private suspend fun loadUserFromNetwork(id: Long, loadAs: UserType = UserType.UNKNOWN): User? = loadUsersFromNetwork(listOf(id), loadAs)[0]

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

    fun getAccountUser() = GlobalScope.async(Dispatchers.IO) {
        if (accountRepository.cachedAccount != null) {
            return@async loadUser(accountRepository.cachedAccount!!.id).await()
        } else {
            return@async null
        }
    }
}