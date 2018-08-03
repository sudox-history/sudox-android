package com.sudox.android.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.Data
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.repository.AccountRepository
import com.sudox.android.common.repository.AuthRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.ProtocolConnectionStabilizer
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                          private val accountRepository: AccountRepository,
                                          private val stabilizer: ProtocolConnectionStabilizer,
                                          private val authRepository: AuthRepository) : ViewModel() {

    val connectLiveData = MutableLiveData<Data<ConnectState>>()

    var connectionDisposable: Disposable = stabilizer.connectionRXData.subscribe {
        connectLiveData.postValue(Data(it))
    }

    fun connect(){
        stabilizer.connectionState().subscribe{
            protocolClient.connect()
        }
    }

    fun sendToken() = authRepository.sendToken(getAccount()?.token)

    fun disconnect() {
        protocolClient.disconnect()
    }

    fun getAccount() = accountRepository.getAccount()

    // Prevent memory leaks with protocol disposables
    override fun onCleared() {
        authRepository.cleanDisposables()
        connectionDisposable.dispose()
    }
}
