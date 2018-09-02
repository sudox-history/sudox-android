package com.sudox.android.ui.auth.confirm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Handler
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.common.repository.auth.AccountRepository
import com.sudox.android.common.repository.auth.AuthRepository
import javax.inject.Inject

class AuthConfirmViewModel @Inject constructor(private val authRepository: AuthRepository,
                                               private val accountRepository: AccountRepository) : ViewModel() {

    var timerData = MutableLiveData<Long>()

    fun sendCode(code: String) = authRepository.sendCode(code)
    fun signIn(code: String) = authRepository.signIn(code)
    fun sendCodeAgain() = authRepository.sendCodeAgain()

    fun setTimer(seconds: Long) {
        var seconds1 = seconds

        Handler().apply {
            val runnable = object : Runnable {
                override fun run() {
                    seconds1--
                    timerData.postValue(seconds1)
                    if(seconds1 != 0L)
                        postDelayed(this, 1000)
                }
            }
            postDelayed(runnable, 0)
        }
    }

    fun saveAccount(id: String, email: String, token: String)
            = accountRepository.saveAccount(SudoxAccount(id, email, token))
}