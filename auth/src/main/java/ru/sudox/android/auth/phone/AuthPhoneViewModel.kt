package ru.sudox.android.auth.phone

import androidx.lifecycle.MutableLiveData
import ru.sudox.android.auth.repositories.AuthRepository
import ru.sudox.android.core.CoreViewModel
import ru.sudox.api.OK_ERROR_CODE
import ru.sudox.api.createApiErrorsCallback
import javax.inject.Inject

class AuthPhoneViewModel @Inject constructor(
        val authRepository: AuthRepository
) : CoreViewModel() {

    val loadingLiveData = MutableLiveData<Boolean>()
    val statusLiveData = MutableLiveData<Int>()

    fun createSession(userPhone: String) {
        loadingLiveData.postValue(true)

        compositeDisposable.add(doRequest(authRepository.createSession(userPhone)).subscribe({
            statusLiveData.postValue(OK_ERROR_CODE)
            loadingLiveData.postValue(false)
        }, createApiErrorsCallback {
            statusLiveData.postValue(it)
            loadingLiveData.postValue(false)
        }))
    }
}