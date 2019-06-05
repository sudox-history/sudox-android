package com.sudox.android.ui.auth.phone

import androidx.lifecycle.ViewModel
import com.sudox.android.data.exceptions.RequestException
import com.sudox.android.data.repositories.users.AuthRepository
import com.sudox.android.ui.auth.phone.enums.AuthEmailAction
import com.sudox.protocol.ProtocolClient
import com.sudox.android.common.helpers.livedata.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthPhoneViewModel @Inject constructor(private val authRepository: AuthRepository,
                                             val protocolClient: ProtocolClient) : ViewModel() {

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
    fun requestCode(phoneNumber: String) = GlobalScope.launch(Dispatchers.IO) {
        authEmailActionLiveData.postValue(AuthEmailAction.FREEZE)

        try {
            authRepository.requestCode(phoneNumber).await()
        } catch (e: RequestException) {
            authErrorsLiveData.postValue(e.errorCode)
        }
    }
}