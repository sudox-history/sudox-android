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
        protocolClient.makeRequest<UserInfoDTO>("users.get", UserInfoDTO().apply {
            this.id = id
        }) { responseCallback(it) }
    }

    /**
     * Получает информацию о пользователе с сервера по E-mail.
     */
    fun searchUser(query: String, responseCallback: (SearchUserDTO) -> (Unit)) {
        protocolClient.makeRequest<SearchUserDTO>("users.search", SearchUserDTO().apply {
            this.query = query
        }) { responseCallback(it) }
    }
}