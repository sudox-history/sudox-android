package com.sudox.android.ui.splash

import android.arch.lifecycle.*
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.ui.splash.enums.SplashAction
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.*
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val accountRepository: AccountRepository) : ViewModel(), LifecycleObserver {

    val splashActionLiveData: SingleLiveEvent<SplashAction> = SingleLiveEvent()

    /**
     * Метод для инициализации соединения и сессии с сервером .
     * Решает на какую Activity переключиться.
     * **/
    fun initSession() = GlobalScope.launch(Dispatchers.IO) {
        val account = accountRepository.getAccount().await()

        // Выполняем нужные действия
        if (account != null) {
            splashActionLiveData.postValue(SplashAction.SHOW_MAIN_ACTIVITY)
        } else {
            splashActionLiveData.postValue(SplashAction.SHOW_AUTH_ACTIVITY)
        }
    }
}
