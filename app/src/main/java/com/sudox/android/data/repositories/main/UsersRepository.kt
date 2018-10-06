package com.sudox.android.data.repositories.main

import com.sudox.android.data.models.users.dto.UserInfoDTO
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepository @Inject constructor(private val protocolClient: ProtocolClient) {

    /**
     * Получает информацию о пользователе с сервера.
     **/
    fun getUser(id: String, successCallback: (UserInfoDTO) -> (Unit)) {
        protocolClient.makeRequest<UserInfoDTO>("users.get", UserInfoDTO().apply {
            this.id = id
        }) { if (it.isSuccess()) successCallback(it) }
    }
}