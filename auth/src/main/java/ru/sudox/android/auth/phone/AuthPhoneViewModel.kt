package ru.sudox.android.auth.phone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ru.sudox.android.auth.repositories.AuthRepository
import ru.sudox.api.NO_INTERNET_CONNECTION
import ru.sudox.api.common.SudoxApi
import ru.sudox.api.exceptions.ApiException
import java.io.IOException
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
                    statusLiveData.postValue(0)
                }, {
                    if (it is ApiException) {
                        statusLiveData.postValue(it.code)
                    } else {
                        statusLiveData.postValue(NO_INTERNET_CONNECTION)
                    }
                })

        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}