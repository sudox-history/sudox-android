package com.sudox.android.ui.auth.register

import android.arch.lifecycle.ViewModel
import com.sudox.android.common.repository.auth.AuthRepository
import com.sudox.android.ui.auth.register.enums.AuthRegisterAction
import com.sudox.protocol.models.SingleLiveEvent
import javax.inject.Inject

class AuthRegisterViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    /**
     * Шина для уведомления View об нужных для выполнения ему действий
     * **/
    val authRegisterActionLiveData = SingleLiveEvent<AuthRegisterAction>()

    /**
     * Отправляет запрос регистрации на сервер.
     * Заказывает действия у View через LiveData в качестве результата
     */
    fun signUp(email: String, code: String, hash: String, name: String, nickname: String) {
        authRegisterActionLiveData.postValue(AuthRegisterAction.FREEZE)

        // Регистрируемся ...
        authRepository.signUp(email, code, hash, name, nickname) {
            if (!it) authRegisterActionLiveData.postValue(AuthRegisterAction.SHOW_ERROR)

            /* Размораживать не нужно, т.к. мы не тупые и сами во View можем догадаться,
               что при ошибке нужно разморозить Fragment ... */
        }
    }
}