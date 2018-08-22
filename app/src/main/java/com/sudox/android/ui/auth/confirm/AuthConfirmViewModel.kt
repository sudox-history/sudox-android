package com.sudox.android.ui.auth.confirm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.common.repository.auth.AccountRepository
import com.sudox.android.common.repository.auth.AuthRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AuthConfirmViewModel @Inject constructor(private val authRepository: AuthRepository,
                                               private val accountRepository: AccountRepository) : ViewModel() {

    var timerData = MutableLiveData<Long>()
    var accountLiveData = MutableLiveData<Boolean>()

    private var disposables: CompositeDisposable = CompositeDisposable()

    // Counter observable for the timer
    private var timerObservable = Observable.interval(1, TimeUnit.SECONDS)
            .startWith(0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil { it == 95L }

    fun sendCode(code: String) = authRepository.sendCode(code)
    fun signIn(code: String) = authRepository.signIn(code)
    fun sendCodeAgain() = authRepository.sendCodeAgain()

    fun setTimer(seconds: Long) {
        disposables.add(timerObservable.subscribe {
            timerData.postValue(seconds - it)
        })
    }

    fun saveAccount(id: String, email: String, token: String) {
        disposables.add(accountRepository.saveAccount(SudoxAccount(id, email, token))
                .observeOn(Schedulers.io())
                .subscribe { accountLiveData.postValue(true) })
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}