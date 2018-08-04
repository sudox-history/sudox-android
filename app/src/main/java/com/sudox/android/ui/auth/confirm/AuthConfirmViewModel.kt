package com.sudox.android.ui.auth.confirm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.repository.AuthRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AuthConfirmViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    private lateinit var disposable : Disposable

    var timerData = MutableLiveData<String>()

    fun sendCode(code: String) = authRepository.sendCode(code)

    fun sendCodeAgain() = authRepository.sendCodeAgain()

    fun setTimer(seconds: Int)  {
        disposable = Single.timer(seconds.toLong(), TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    timerData.postValue(it.toString())
                })
    }


    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}