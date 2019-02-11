package com.sudox.android.ui.auth.register

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.exceptions.RequestException
import com.sudox.android.data.exceptions.RequestRegexException
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.ui.auth.register.enums.AuthRegisterAction
import com.sudox.android.common.livedata.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthRegisterViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    /**
     * Шина для уведомления View об нужных для выполнения ему действий
     * **/
    val authRegisterActionLiveData = SingleLiveEvent<AuthRegisterAction>()
    val authRegisterRegexErrorsLiveData = SingleLiveEvent<List<Int>>()
    val authRegisterErrorsLiveData = SingleLiveEvent<Int>()

    /**
     * Отправляет запрос регистрации на сервер.
     * Заказывает действия у View через LiveData в качестве результата
     */
    fun signUp(phoneNumber: String, code: String, hash: String, name: String, nickname: String) = GlobalScope.launch(Dispatchers.IO) {
        authRegisterActionLiveData.postValue(AuthRegisterAction.FREEZE)

        // Регистрируемся ...
        try {
            authRepository.signUp(phoneNumber, code, hash, name, nickname).await()
        } catch (e: RequestRegexException) {
            authRegisterRegexErrorsLiveData.postValue(e.fields)
        } catch (e: RequestException) {
            if (e.errorCode == Errors.CODE_EXPIRED || e.errorCode == Errors.WRONG_CODE) {
                authRegisterActionLiveData.postValue(AuthRegisterAction.SHOW_EMAIL_FRAGMENT_WITH_CODE_EXPIRED_ERROR)
            } else if (e.errorCode == Errors.INVALID_ACCOUNT) {
                authRegisterActionLiveData.postValue(AuthRegisterAction.SHOW_EMAIL_FRAGMENT_WITH_INVALID_ACCOUNT_ERROR)
            } else {
                authRegisterErrorsLiveData.postValue(e.errorCode)
            }
        }
    }
}