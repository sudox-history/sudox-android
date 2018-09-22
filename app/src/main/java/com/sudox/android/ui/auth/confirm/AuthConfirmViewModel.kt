package com.sudox.android.ui.auth.confirm

import android.arch.lifecycle.ViewModel
import com.sudox.android.common.repository.auth.AuthRepository
import com.sudox.android.ui.auth.confirm.enums.AuthConfirmAction
import com.sudox.protocol.models.SingleLiveEvent
import javax.inject.Inject

class AuthConfirmViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    /**
     * Шина для уведомления View об нужных для выполнения ему действий
     * **/
    val authConfirmActionLiveData = SingleLiveEvent<AuthConfirmAction>()

    /**
     * Метод, запрашивающий проверку кода на сервере ...
     *
     * Важно! Вызывать только если статус регистрации == 0
     * **/
    fun checkCode(email: String, code: String, hash: String) {
        authConfirmActionLiveData.postValue(AuthConfirmAction.FREEZE)

        // Запрос проверки кода ...
        authRepository.checkCode(email, code, hash) {
            if (!it) {
                authConfirmActionLiveData.postValue(AuthConfirmAction.SHOW_ERROR)
            } else {
                authConfirmActionLiveData.postValue(AuthConfirmAction.SHOW_REGISTRATION)
            }
        }
    }

    /**
     * Метод, запрашивающий проверку кода на сервере и выполняющий авторизацию ...
     *
     * Важно! Вызывать только если статус регистрации == 1
     * **/
    fun signIn(email: String, code: String, hash: String) {
        authConfirmActionLiveData.postValue(AuthConfirmAction.FREEZE)

        // Запрос проверки кода и авторизации ...
        authRepository.signIn(email, code, hash) {
            if (!it) authConfirmActionLiveData.postValue(AuthConfirmAction.SHOW_ERROR)
        }
    }
}