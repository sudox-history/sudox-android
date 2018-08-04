package com.sudox.android.ui.auth.register

import androidx.lifecycle.ViewModel
import com.sudox.android.common.repository.AccountRepository
import com.sudox.android.common.repository.AuthRepository
import javax.inject.Inject

class AuthRegisterViewModel @Inject constructor(private val authRepository: AuthRepository,
                                                private val accountRepository: AccountRepository) : ViewModel() {

    fun sendUserData(name: String, surname: String) = authRepository.sendUserData(name, surname)
}