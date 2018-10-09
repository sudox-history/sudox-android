package com.sudox.android.ui.splash

import android.arch.lifecycle.*
import com.sudox.android.data.auth.AUTH_ACCOUNT_MANAGER_START
import com.sudox.protocol.models.enums.ConnectionState
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.ui.splash.enums.SplashAction
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                          private val accountRepository: AccountRepository,
                                          private val authRepository: AuthRepository) : ViewModel(), LifecycleObserver {

    val splashActionLiveData: SingleLiveEvent<SplashAction> = SingleLiveEvent()

    // Переменная-метка. Нужна для предотвращения рекурсии
    private var blocked: Boolean = false

    /**
     * Метод для инициализации соединения и сессии с сервером .
     * Решает на какую Activity переключиться.
     * **/
    fun initSession(lifecycleOwner: LifecycleOwner, authKey: Int) = async {
        val account = accountRepository.getAccount().await()

        // Handle connection status ...
        protocolClient.connectionStateLiveData.observe(lifecycleOwner, Observer {
            if ((it == ConnectionState.CONNECT_ERRORED || it == ConnectionState.CONNECTION_CLOSED) && !blocked) {
                blocked = true

                // Выполняем нужные действия
                if (authKey == AUTH_ACCOUNT_MANAGER_START && account != null) {
                    splashActionLiveData.postValue(SplashAction.SHOW_ACCOUNT_EXISTS_ALERT)
                } else if (account != null) {
                    splashActionLiveData.postValue(SplashAction.SHOW_MAIN_ACTIVITY)
                } else {
                    splashActionLiveData.postValue(SplashAction.SHOW_AUTH_ACTIVITY)
                }
            }
        })

        // Handle session status ...
        authRepository.accountSessionLiveData.observe(lifecycleOwner, Observer {
            if (blocked) return@Observer

            // Избегаем конфликтов
            blocked = true

            if (it?.lived!!) {
                if (authKey == AUTH_ACCOUNT_MANAGER_START) {
                    splashActionLiveData.postValue(SplashAction.SHOW_ACCOUNT_EXISTS_ALERT)
                } else {
                    splashActionLiveData.postValue(SplashAction.SHOW_MAIN_ACTIVITY)
                }
            } else {
                splashActionLiveData.postValue(SplashAction.SHOW_AUTH_ACTIVITY)
            }
        })

        // Start async connection ...
        protocolClient.connect()
    }

    /**
     * Просто метод для закрытия соединения с сервером. Ничего сложного :)
     * **/
    fun closeConnection() = protocolClient.close()
}
