package com.sudox.android.ui.auth.email

import androidx.lifecycle.ViewModel
import com.sudox.android.common.repository.auth.AuthRepository
import javax.inject.Inject

class AuthEmailViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    fun sendEmail(email: String) = authRepository.sendEmail(email)
}