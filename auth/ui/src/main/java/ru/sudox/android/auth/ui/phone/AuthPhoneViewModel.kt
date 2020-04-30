package ru.sudox.android.auth.ui.phone

import androidx.lifecycle.MutableLiveData
import ru.sudox.android.auth.data.entities.AuthSessionEntity
import ru.sudox.android.auth.data.entities.AuthSessionStage
import ru.sudox.android.auth.data.repositories.AuthRepository
import ru.sudox.android.core.CoreViewModel
import ru.sudox.android.core.livedata.SingleLiveEvent
import javax.inject.Inject

class AuthPhoneViewModel @Inject constructor(
        private val authRepository: AuthRepository
) : CoreViewModel() {

    val successLiveData = SingleLiveEvent<AuthSessionEntity?>()
    val loadingStateLiveData = MutableLiveData<Boolean>()
    val errorsLiveData = MutableLiveData<Throwable>()

    fun createSession(userPhone: String) {
        loadingStateLiveData.postValue(true)

        compositeDisposable.add(doRequest(authRepository.createOrRestoreSession(userPhone)).subscribe({
            errorsLiveData.postValue(null)
            successLiveData.postValue(it)
            loadingStateLiveData.postValue(false)
        }, {
            errorsLiveData.postValue(it)
            successLiveData.postValue(null)
            loadingStateLiveData.postValue(false)
        }))
    }
}