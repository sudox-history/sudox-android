package ru.sudox.android.auth.ui.signup

import androidx.lifecycle.MutableLiveData
import ru.sudox.android.auth.data.repositories.AuthRepository
import ru.sudox.android.core.CoreViewModel
import ru.sudox.android.core.livedata.SingleLiveEvent
import javax.inject.Inject

class AuthSignUpViewModel @Inject constructor(
        private val authRepository: AuthRepository
) : CoreViewModel() {

    val successLiveData = SingleLiveEvent<Unit>()
    val loadingStateLiveData = MutableLiveData<Boolean>()
    val errorsLiveData = MutableLiveData<Throwable>()

    fun signUp(name: String, nickname: String) {
        loadingStateLiveData.postValue(true)

        compositeDisposable.add(doRequest(authRepository.signUp(name, nickname)).subscribe({
            successLiveData.postValue(null)
            loadingStateLiveData.postValue(false)
        }, {
            errorsLiveData.postValue(it)
            loadingStateLiveData.postValue(false)
        }))
    }
}