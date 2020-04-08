package ru.sudox.android.auth.phone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.internal.operators.observable.throttleLastIncludingErrors
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.sudox.android.auth.repositories.AuthRepository
import ru.sudox.api.OK_ERROR_CODE
import ru.sudox.api.createApiErrorsCallback
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthPhoneViewModel @Inject constructor(
        val authRepository: AuthRepository
) : ViewModel() {

    val compositeDisposable = CompositeDisposable()
    val loadingLiveData = MutableLiveData<Boolean>()
    val statusLiveData = MutableLiveData<Int>()

    fun createSession(userPhone: String) {
        loadingLiveData.postValue(true)

        val disposable = authRepository
                .createSession(userPhone)
                .throttleLastIncludingErrors(400, TimeUnit.MILLISECONDS)
                .subscribe({
                    loadingLiveData.postValue(false)
                    statusLiveData.postValue(OK_ERROR_CODE)
                }, createApiErrorsCallback {
                    loadingLiveData.postValue(false)
                    statusLiveData.postValue(it)
                })

        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}