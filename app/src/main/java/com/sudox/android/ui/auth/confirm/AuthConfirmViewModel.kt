package com.sudox.android.ui.auth.confirm

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.models.Errors
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.ui.auth.confirm.enums.AuthConfirmAction
import com.sudox.protocol.models.SingleLiveEvent
import javax.inject.Inject

class AuthConfirmViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    /**
     * Шина для уведомления View об нужных для выполнения ему действий
     * **/
    val authConfirmActionLiveData = SingleLiveEvent<AuthConfirmAction>()
    val authConfirmErrorsLiveData = SingleLiveEvent<Int>()

    /**
     * Метод, запрашивающий проверку кода на сервере ...
     *
     * Важно! Вызывать только если статус регистрации == 0
     * **/
    fun checkCode(email: String, code: String, hash: String) {
        authConfirmActionLiveData.postValue(AuthConfirmAction.FREEZE)

        // Запрос проверки кода ...
        authRepository.checkCode(email, code, hash, {
            authConfirmActionLiveData.postValue(AuthConfirmAction.SHOW_REGISTER_FRAGMENT)
        }, {
            if (it == Errors.CODE_EXPIRED) {
                authConfirmActionLiveData.postValue(AuthConfirmAction.SHOW_EMAIL_FRAGMENT_WITH_CODE_EXPIRED_ERROR)
            } else {
                authConfirmErrorsLiveData.postValue(it)
            }
        })
    }

    /**
     * Метод, запрашивающий проверку кода на сервере и выполняющий авторизацию ...
     *
     * Важно! Вызывать только если статус регистрации == 1
     * **/
    fun signIn(email: String, code: String, hash: String) {
        authConfirmActionLiveData.postValue(AuthConfirmAction.FREEZE)

        // Запрос проверки кода и авторизации ...
        authRepository.signIn(email, code, hash, {}, {
            if (it == Errors.CODE_EXPIRED) {
                authConfirmActionLiveData.postValue(AuthConfirmAction.SHOW_EMAIL_FRAGMENT_WITH_CODE_EXPIRED_ERROR)
            } else {
                authConfirmErrorsLiveData.postValue(it)
            }
        })
    }
}