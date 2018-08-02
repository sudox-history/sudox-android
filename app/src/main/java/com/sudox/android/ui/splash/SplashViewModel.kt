package com.sudox.android.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.Data
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.enums.HandshakeState
import com.sudox.android.common.models.TokenData
import com.sudox.android.common.repository.AccountRepository
import com.sudox.android.common.repository.AuthRepository
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                          private val accountRepository: AccountRepository,
                                          private val authRepository: AuthRepository) : ViewModel() {

    lateinit var connectData: LiveData<Data<ConnectState>>
    lateinit var handshakeData: LiveData<Data<HandshakeState>>
    lateinit var tokenData: LiveData<TokenData>

    fun connect() = authRepository.connect()

    fun startHandshake() = authRepository.startHandshake()

    fun sendToken() = authRepository.sendToken(getAccount()?.token)

    fun disconnect(){
        protocolClient.disconnect()
    }

    fun getAccount() = accountRepository.getAccount()

    // Prevent memory leaks with protocol disposables
    override fun onCleared() {
        authRepository.cleanDisposables()
    }
}
