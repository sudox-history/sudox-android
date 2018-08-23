package com.sudox.android.ui.auth.register

import androidx.lifecycle.ViewModel
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.common.repository.auth.AccountRepository
import com.sudox.android.common.repository.auth.AuthRepository
import javax.inject.Inject

class AuthRegisterViewModel @Inject constructor(private val authRepository: AuthRepository,
                                                private val accountRepository: AccountRepository) : ViewModel() {

    fun sendUserData(name: String, nickname: String) = authRepository.signUp(name, nickname)

    fun saveAccount(id: String, email: String, token: String)
            = accountRepository.saveAccount(SudoxAccount(id, email, token))
}