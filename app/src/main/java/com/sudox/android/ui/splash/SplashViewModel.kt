package com.sudox.android.ui.splash

import androidx.lifecycle.ViewModel
import com.sudox.android.common.repository.AccountRepository
import com.sudox.android.common.repository.AuthRepository
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                          private val accountRepository: AccountRepository,
                                          private val authRepository: AuthRepository) : ViewModel() {

    fun connect() = authRepository.connect()

    fun startHandshake() = authRepository.startHandshake()

    fun sendToken() = authRepository.sendToken(getAccount()?.token)

    fun disconnect() {
        protocolClient.disconnect()
    }

    fun getAccount() = accountRepository.getAccount()

    // Prevent memory leaks with protocol disposables
    override fun onCleared() {
        authRepository.cleanDisposables()
    }
}
