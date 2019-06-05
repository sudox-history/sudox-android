package com.sudox.android.ui.splash

import androidx.lifecycle.*
import com.sudox.android.ui.splash.enums.SplashAction
import com.sudox.android.common.helpers.livedata.SingleLiveEvent
import com.sudox.android.data.repositories.users.AccountRepository
import kotlinx.coroutines.*
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val accountRepository: AccountRepository) : ViewModel() {

    val splashActionLiveData: SingleLiveEvent<SplashAction> = SingleLiveEvent()

    /**
     * Проверяет наличие аккаунта, далее решает какую Activity нужно открыть.
     */
    fun checkAccount() = GlobalScope.launch(Dispatchers.IO) {
        if (accountRepository.getAccount() != null) {
            splashActionLiveData.postValue(SplashAction.SHOW_MAIN_ACTIVITY)
        } else {
            splashActionLiveData.postValue(SplashAction.SHOW_AUTH_ACTIVITY)
        }
    }
}
