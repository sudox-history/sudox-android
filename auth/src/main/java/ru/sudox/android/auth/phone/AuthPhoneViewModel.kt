package ru.sudox.android.auth.phone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ru.sudox.android.auth.repositories.AuthRepository
import ru.sudox.api.OK_ERROR_CODE
import ru.sudox.api.createApiErrorsCallback
import javax.inject.Inject

class AuthPhoneViewModel @Inject constructor(
        val authRepository: AuthRepository
) : ViewModel() {

    val compositeDisposable = CompositeDisposable()
    val statusLiveData = MutableLiveData<Int>()

    fun createSession(userPhone: String) {
        val disposable = authRepository
                .createSession(userPhone)
                .subscribe({
                    statusLiveData.postValue(OK_ERROR_CODE)
                }, createApiErrorsCallback {
                    statusLiveData.postValue(it)
                })

        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}