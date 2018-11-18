package com.sudox.android.data.repositories.main

import com.sudox.android.data.models.users.dto.SearchUserDTO
import com.sudox.android.data.models.users.dto.UserInfoDTO
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepository @Inject constructor(private val protocolClient: ProtocolClient) {

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
    fun getUsers(ids: List<String>, responseCallback: (UserInfoDTO) -> Unit) {
        protocolClient.makeRequest<UserInfoDTO>("users.getUsers", UserInfoDTO().apply {
            this.ids = ids
        }) { responseCallback(it) }
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