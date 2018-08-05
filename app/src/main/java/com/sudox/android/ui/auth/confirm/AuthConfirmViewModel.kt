package com.sudox.android.ui.auth.confirm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.common.repository.AccountRepository
import com.sudox.android.common.repository.AuthRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AuthConfirmViewModel @Inject constructor(private val authRepository: AuthRepository,
                                               private val accountRepository: AccountRepository) : ViewModel() {

    private lateinit var disposable: Disposable

    var timerData = MutableLiveData<Long>()
    private var timerObservable = Observable.interval(1, TimeUnit.SECONDS)
            .startWith(0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil { it == 95L }

    fun sendCode(code: String) = authRepository.sendCode(code)

    fun signIn(code: String) = authRepository.signIn(code)

    fun sendCodeAgain() = authRepository.sendCodeAgain()

    fun setTimer(seconds: Long) {
        disposable = timerObservable.subscribe {
            timerData.postValue(seconds - it)
        }
    }

    fun saveAccount(id: Long, token: String) {
        accountRepository.saveAccount(SudoxAccount(id, "Sudox", token))
    }

    override fun onCleared() {
        disposable.dispose()

        super.onCleared()
    }
}