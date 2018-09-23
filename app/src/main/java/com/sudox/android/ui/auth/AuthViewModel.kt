package com.sudox.android.ui.auth

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class AuthViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                        private val authRepository: AuthRepository) : ViewModel() {

    // Шины событий
    val connectionStateLiveData = protocolClient.connectionStateLiveData
    val authSessionStateLiveData = authRepository.authSessionLiveData
    val accountSessionLiveData = authRepository.accountSessionLiveData

    /**
     * Просто метод для закрытия соединения с сервером. Ничего сложного :)
     * **/
    fun closeConnection() = protocolClient.close()
}