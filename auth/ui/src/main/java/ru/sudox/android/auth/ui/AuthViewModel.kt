package ru.sudox.android.auth.ui

import androidx.lifecycle.MutableLiveData
import ru.sudox.android.auth.data.repositories.AuthRepository
import ru.sudox.android.core.CoreViewModel
import ru.sudox.android.core.livedata.SingleLiveEvent

open class AuthViewModel(
        subscribeToTimer: Boolean,
        authRepository: AuthRepository,
        pushSessionErrorsToErrorsLiveData: Boolean
) : CoreViewModel() {

    val sessionErrorLiveData = SingleLiveEvent<Throwable>()
    val loadingStateLiveData = MutableLiveData<Boolean>()
    val errorsLiveData = MutableLiveData<Throwable>()

    init {
        compositeDisposable.add(authRepository.authSessionErrorsSubject.subscribe {
            if (pushSessionErrorsToErrorsLiveData) {
                errorsLiveData.postValue(it)
            } else {
                sessionErrorLiveData.postValue(it)
            }
        })

        if (subscribeToTimer) {
            compositeDisposable.add(authRepository.timerObservable!!.subscribe())
        }
    }

    override fun onCleared() {
        super.onCleared()

        // TODO: Session closed
    }
}