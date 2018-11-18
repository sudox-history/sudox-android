package com.sudox.android.data.repositories.main

import com.sudox.android.common.userUnknown
import com.sudox.android.data.database.dao.UserDao
import com.sudox.android.data.database.model.User
import com.sudox.android.data.models.users.dto.SearchUserDTO
import com.sudox.android.data.models.users.dto.UserInfoDTO
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                          private val userDao: UserDao) {

    /**
     * Получает информацию о пользователе с сервера по ID.
     **/
    fun getUser(id: String, responseCallback: (UserInfoDTO) -> (Unit)) {
        protocolClient.makeRequest<UserInfoDTO>("users.getUser", UserInfoDTO().apply {
            this.id = id
        }) { responseCallback(it) }
    }

    /**
     * Получить информацию о пользователях.
     */
    fun getUsers(ids: List<String>, callback: (List<User>) -> Unit) {
        protocolClient.makeRequest<UserInfoDTO>("users.getUsers", UserInfoDTO().apply {
            this.ids = ids
        }) {
            if (it.containsError()) return@makeRequest

            // List of users
            val storableUsers = userDao.getUsers(ids)
            val newUsers = it.users.map {
                val foundedUser = storableUsers.find { it.uid == it.uid }

                User().apply {
                    uid = it.id
                    name = it.name
                    nickname = it.nickname
                    avatar = it.photo
                    phone = it.phone
                    status = it.status
                    bio = it.bio
                    type = foundedUser?.type ?: userUnknown
                }
            }

            // Save to database
            userDao.insertAll(newUsers)

            // Return as result
            callback(newUsers)
        }
    }

    /**
     * Получает информацию о пользователе с сервера по E-mail.
     */
    @Deprecated("Поиска в приложении больше не существует")
    fun searchUser(query: String, responseCallback: (SearchUserDTO) -> (Unit)) {
        protocolClient.makeRequest<SearchUserDTO>("users.search", SearchUserDTO().apply {
            this.query = query
        }) { responseCallback(it) }
    }
}