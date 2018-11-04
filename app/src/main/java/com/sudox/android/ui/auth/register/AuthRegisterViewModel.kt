package com.sudox.android.ui.auth.register

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.models.Errors
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.ui.auth.register.enums.AuthRegisterAction
import com.sudox.protocol.models.SingleLiveEvent
import javax.inject.Inject

class AuthRegisterViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    /**
     * Шина для уведомления View об нужных для выполнения ему действий
     * **/
    val authRegisterActionLiveData = SingleLiveEvent<AuthRegisterAction>()
    val authRegisterRegexErrorsLiveData = SingleLiveEvent<Int>()
    val authRegisterErrorsLiveData = SingleLiveEvent<Int>()

    /**
     * Отправляет запрос регистрации на сервер.
     * Заказывает действия у View через LiveData в качестве результата
     */
    fun signUp(email: String, code: String, hash: String, name: String, nickname: String) {
        authRegisterActionLiveData.postValue(AuthRegisterAction.FREEZE)

        // Регистрируемся ...
        authRepository.signUp(email, code, hash, name, nickname, {
            authRegisterRegexErrorsLiveData.postValue(it)
        }, {}, {
            if (it == Errors.CODE_EXPIRED || it == Errors.WRONG_CODE) {
                authRegisterActionLiveData.postValue(AuthRegisterAction.SHOW_EMAIL_FRAGMENT_WITH_CODE_EXPIRED_ERROR)
            } else if (it == Errors.INVALID_ACCOUNT) {
                authRegisterActionLiveData.postValue(AuthRegisterAction.SHOW_EMAIL_FRAGMENT_WITH_INVALID_ACCOUNT_ERROR)
            } else {
                authRegisterErrorsLiveData.postValue(it)
            }
        })
    }
}