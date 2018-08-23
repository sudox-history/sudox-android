package com.sudox.android.ui.auth

import androidx.lifecycle.ViewModel
import com.sudox.android.common.repository.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class AuthViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                        private val authRepository: AuthRepository) : ViewModel() {

    var connectLiveData = protocolClient.connectionStateLiveData

    fun importAuthHash(hash: String) = authRepository.importAuthHash(hash)
    fun disconnect() = protocolClient.disconnect()
}