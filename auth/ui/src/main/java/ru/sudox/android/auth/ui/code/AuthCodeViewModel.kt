package ru.sudox.android.auth.ui.code

import androidx.lifecycle.MutableLiveData
import ru.sudox.android.auth.data.repositories.AuthRepository
import ru.sudox.android.core.CoreViewModel
import ru.sudox.android.core.livedata.SingleLiveEvent
import ru.sudox.api.createApiErrorsCallback
import javax.inject.Inject

class AuthCodeViewModel @Inject constructor(
        val authRepository: AuthRepository
) : CoreViewModel() {

    val loadingLiveData = MutableLiveData<Boolean>()
    val successLiveData = SingleLiveEvent<Nothing>()
    val errorsLiveData = MutableLiveData<Int>()

    fun checkCode(code: Int) {
        loadingLiveData.postValue(true)

        compositeDisposable.add(doRequest(authRepository.checkCode(code)).subscribe({
            successLiveData.postValue(null)
            loadingLiveData.postValue(false)
            errorsLiveData.postValue(null)
        }, createApiErrorsCallback {
            errorsLiveData.postValue(it)
            loadingLiveData.postValue(false)
        }))
    }
}