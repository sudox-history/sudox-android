package com.sudox.android.ui.splash

import android.arch.lifecycle.*
import com.sudox.android.common.auth.AUTH_ACCOUNT_MANAGER_START
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.repository.auth.AccountRepository
import com.sudox.android.ui.splash.enums.SplashAction
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                          private val accountRepository: AccountRepository) : ViewModel(), LifecycleObserver {

    val splashActionLiveData: SingleLiveEvent<SplashAction> = SingleLiveEvent()

    /**
     * Метод для инициализации соединения и сессии с сервером .
     * Решает на какую Activity переключиться.
     * **/
    fun initSession(lifecycleOwner: LifecycleOwner, authKey: Int) = async {
        val account = accountRepository.getAccount().await()

        if (account != null) {
            accountRepository.setSessionData(account)
        } else {
            accountRepository.clearSessionData()
        }

        // Handle connection status ...
        protocolClient.connectionStateLiveData.observe(lifecycleOwner, Observer {
            if (it == ConnectState.CONNECT_ERROR) {
                if (authKey == AUTH_ACCOUNT_MANAGER_START && account != null) {
                    splashActionLiveData.postValue(SplashAction.SHOW_ACCOUNT_EXISTS_ALERT)
                } else {
                    splashActionLiveData.postValue(SplashAction.SHOW_AUTH_ACTIVITY)
                }
            } else if (it == ConnectState.MISSING_TOKEN || it == ConnectState.WRONG_TOKEN) {
                runBlocking { accountRepository.removeAccounts().await() }
                splashActionLiveData.postValue(SplashAction.SHOW_AUTH_ACTIVITY)
            } else if (it == ConnectState.CORRECT_TOKEN) {
                if (authKey == AUTH_ACCOUNT_MANAGER_START) {
                    splashActionLiveData.postValue(SplashAction.SHOW_ACCOUNT_EXISTS_ALERT)
                } else {
                    splashActionLiveData.postValue(SplashAction.SHOW_MAIN_ACTIVITY)
                }
            }
        })

        // Start async connection ...
        protocolClient.connect()
    }
}
