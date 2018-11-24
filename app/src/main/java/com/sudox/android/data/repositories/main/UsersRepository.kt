package com.sudox.android.data.repositories.main

import com.sudox.android.data.database.dao.UserDao
import com.sudox.android.data.database.model.User
import com.sudox.android.data.models.Errors
import com.sudox.android.data.models.users.UserType
import com.sudox.android.data.models.users.dto.UserInfoDTO
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class UsersRepository @Inject constructor(private val authRepository: AuthRepository,
                                          private val protocolClient: ProtocolClient,
                                          private val userDao: UserDao) {

    // ID загруженных пользователей (для экономии трафика)
    private val loadedUsersIds: HashSet<String> = HashSet()

    init {
        listenConnectionStatus()
    }

    private fun listenConnectionStatus() = authRepository.accountSessionLiveData.observeForever {
        if (it!!.lived) loadedUsersIds.clear()
    }

    fun loadUsers(ids: List<String>) = GlobalScope.async {
        if (protocolClient.isValid()) {
            val usersFromNetwork = loadUsersFromNetwork(ids.filter { !loadedUsersIds.contains(it) })
            val usersFromNetworkIds = usersFromNetwork.map { it.uid }
            val usersFromDatabase = userDao.loadByIds(ids.filter { !usersFromNetworkIds.contains(it) })

            // Combine network & database founded contacts
            return@async usersFromNetwork.plus(usersFromDatabase)
        } else {
            return@async userDao.loadByIds(ids)
        }
    }

    fun loadUser(id: String) = GlobalScope.async {
        if (protocolClient.isValid() && !loadedUsersIds.contains(id)) {
            return@async loadUserFromNetwork(id)
        } else {
            return@async userDao.loadById(id)
        }
    }

    private suspend fun loadUsersFromNetwork(ids: List<String>): List<User> = suspendCoroutine { continuation ->
        protocolClient.makeRequest<UserInfoDTO>("users.getUsers", UserInfoDTO().apply {
            this.ids = ids
        }) {
            if (!it.users.isEmpty() && !(it.containsError())) {
                // Map users to database format
                val users = toStorableUsers(it)

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

    private suspend fun loadUserFromNetwork(id: String): User? = suspendCoroutine { continuation ->
        protocolClient.makeRequest<UserInfoDTO>("users.getUser", UserInfoDTO().apply {
            this.id = id
        }) {
            if (!it.containsError()) {
                val user = toStorableUser(it)

                // Cache user
                userDao.insertOne(user)
                loadedUsersIds.plusAssign(user.uid)

                // Return result
                continuation.resume(user)
            } else if (it.error == Errors.INVALID_USER) {
                userDao.removeOne(id)

                // null is optional result!
                continuation.resume(null)
            } else {
                continuation.resume(null)
            }
        }
    }

    private fun toStorableUsers(userInfoDTO: UserInfoDTO): List<User> {
        return userInfoDTO.users.map { toStorableUser(it) }
    }

    private fun toStorableUser(userInfoDTO: UserInfoDTO): User {
        return User(userInfoDTO.id,
                userInfoDTO.name,
                userInfoDTO.nickname,
                userInfoDTO.photo,
                userInfoDTO.phone,
                userInfoDTO.status,
                userInfoDTO.bio,
                UserType.UNKNOWN)
    }
}