package com.sudox.android.ui.auth.confirm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.repository.AuthRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AuthConfirmViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private lateinit var disposable: Disposable

    var timerData = MutableLiveData<Long>()
    var timerObservable = Observable.interval(1, TimeUnit.SECONDS)
            .startWith(0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil { it == 95L }

    fun sendCode(code: String) = authRepository.sendCode(code)

    fun sendCodeAgain() = authRepository.sendCodeAgain()

    fun setTimer(seconds: Long) {
        disposable = timerObservable.subscribe {
            timerData.postValue(seconds - it)
        }
    }


    override fun onCleared() {
        disposable.dispose()

        super.onCleared()
    }
}