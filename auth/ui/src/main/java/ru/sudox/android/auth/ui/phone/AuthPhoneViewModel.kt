package ru.sudox.android.auth.ui.phone

import androidx.lifecycle.MutableLiveData
import ru.sudox.android.auth.data.repositories.AuthRepository
import ru.sudox.android.core.CoreViewModel
import ru.sudox.android.core.livedata.SingleLiveEvent
import ru.sudox.api.createApiErrorsCallback
import javax.inject.Inject

class AuthPhoneViewModel @Inject constructor(
        val authRepository: AuthRepository
) : CoreViewModel() {

    val loadingLiveData = MutableLiveData<Boolean>()
    val successLiveData = SingleLiveEvent<Nothing>()
    val errorsLiveData = MutableLiveData<Int>()

    fun createSession(userPhone: String) {
        loadingLiveData.postValue(true)

        compositeDisposable.add(doRequest(authRepository.createSessionOrRestore(userPhone)).subscribe({
            successLiveData.postValue(null)
            loadingLiveData.postValue(false)
            errorsLiveData.postValue(null)
        }, createApiErrorsCallback {
            errorsLiveData.postValue(it)
            loadingLiveData.postValue(false)
        }))
    }
}