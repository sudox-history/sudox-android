package com.sudox.android.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.models.dto.AuthSessionDTO
import com.sudox.android.common.repository.AuthRepository
import javax.inject.Inject

class AuthEmailViewModel @Inject constructor(val authRepository: AuthRepository) : ViewModel() {

    fun sendEmail(email: String): LiveData<AuthSessionDTO> {
        return authRepository.sendEmail(email)
    }
}