package com.sudox.android.ui.auth.email

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.ui.auth.email.enums.AuthEmailAction
import com.sudox.protocol.models.SingleLiveEvent
import javax.inject.Inject

class AuthEmailViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    /**
     * Шина для уведомления View об нужных для выполнения ему действий
     * **/
    val authEmailActionLiveData = SingleLiveEvent<AuthEmailAction>()
    val authErrorsLiveData = SingleLiveEvent<Int>()

    /**
     * Метод для запроса отправки кода на указанный E-mail.
     *
     * Во ViewModel обрабатывает только ошибки, состояние сессии передается по LiveData до AuthActivity,
     * там уже и происходит переключение на AuthConfirmFragment.
     * */
    fun requestCode(email: String) {
        authEmailActionLiveData.postValue(AuthEmailAction.FREEZE)
        authRepository.requestCode(email, {}, { authErrorsLiveData.postValue(it) })
    }
}