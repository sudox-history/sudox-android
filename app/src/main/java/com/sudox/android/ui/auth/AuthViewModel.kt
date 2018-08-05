package com.sudox.android.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.Data
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.repository.AuthRepository
import com.sudox.protocol.ProtocolClient
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class AuthViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                        private val authRepository: AuthRepository) : ViewModel() {

    var connectLiveData = MutableLiveData<Data<ConnectState>>()

    // Connection controller
    var connectionDisposable: Disposable = protocolClient.connectionSubject.subscribe {
        connectLiveData.postValue(Data(it))
    }

    fun importAuthHash(hash: String) = authRepository.importAuthHash(hash)

    fun disconnect() {
        protocolClient.disconnect()
    }

    override fun onCleared() {
        connectionDisposable.dispose()
        super.onCleared()
    }
}