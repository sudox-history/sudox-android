package com.sudox.android.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.Data
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.repository.auth.AccountRepository
import com.sudox.android.common.repository.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                          private val accountRepository: AccountRepository,
                                          private val authRepository: AuthRepository) : ViewModel() {

    val connectLiveData = MutableLiveData<Data<ConnectState>>()
    val accountLiveData = MutableLiveData<SudoxAccount?>()

    private var connectionDisposable: Disposable = protocolClient.connectionSubject
            .subscribeOn(Schedulers.io())
            .subscribe {
                connectLiveData.postValue(Data(it))
            }

    private var accountDisposable: Disposable = accountRepository.getAccount()
            .subscribeOn(Schedulers.io())
            .subscribe(Consumer {
                if (it == null)
                    accountRepository.deleteData()
                accountLiveData.postValue(it)

            })

    fun connect() = protocolClient.connect()
    fun sendToken(sudoxAccount: SudoxAccount?) = authRepository.sendToken(sudoxAccount?.token)
    fun disconnect() = protocolClient.disconnect()

    // Prevent memory leaks with protocol disposables
    override fun onCleared() {
        authRepository.cleanDisposables()
        connectionDisposable.dispose()
        accountDisposable.dispose()
    }
}
