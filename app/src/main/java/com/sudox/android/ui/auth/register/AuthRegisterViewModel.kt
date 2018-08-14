package com.sudox.android.ui.auth.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.common.repository.auth.AccountRepository
import com.sudox.android.common.repository.auth.AuthRepository
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AuthRegisterViewModel @Inject constructor(private val authRepository: AuthRepository,
                                                private val accountRepository: AccountRepository) : ViewModel() {

    var accountLiveData = MutableLiveData<Boolean>()

    // I was being used nullable, because this variable may be doesn't initialized (TheMax, 14.08.2018)
    private var accountDisposable: Disposable? = null

    fun sendUserData(name: String, nickname: String) = authRepository.signUp(name, nickname)

    fun saveAccount(id: String, email: String, token: String) {
        accountDisposable = accountRepository.saveAccount(SudoxAccount(id, email, token))
                .observeOn(Schedulers.io())
                .subscribe { accountLiveData.postValue(true) }
    }

    override fun onCleared() {
        accountDisposable?.dispose()

        // Super!
        super.onCleared()
    }
}