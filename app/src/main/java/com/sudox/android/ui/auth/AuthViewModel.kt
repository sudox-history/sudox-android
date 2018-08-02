package com.sudox.android.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.Data
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.repository.AuthRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.ProtocolConnectionStabilizer
import com.sudox.protocol.model.ConnectionStateCallback
import javax.inject.Inject

class AuthViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                        private val authRepository: AuthRepository,
                                        private val stabilizer: ProtocolConnectionStabilizer): ViewModel(), ConnectionStateCallback {

    var connectData = MutableLiveData<Data<ConnectState>>()

    init {
        stabilizer.subscribe(this)
    }

    fun disconnect(){
        protocolClient.disconnect()
    }

    override fun onReconnect() {
        connectData = authRepository.startHandshake() as MutableLiveData<Data<ConnectState>>
        TODO("the method does not work... fix that")
    }

    override fun onDisconnect() {
        connectData.postValue(Data(ConnectState.DISCONNECT))
    }

    override fun onCleared() {
        stabilizer.unsubscribe(this)
    }
}