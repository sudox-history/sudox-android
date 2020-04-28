package ru.sudox.android.auth.ui.phone

import ru.sudox.android.auth.data.repositories.AuthRepository
import ru.sudox.android.core.CoreViewModel
import javax.inject.Inject

class AuthPhoneViewModel @Inject constructor(
        private val authRepository: AuthRepository
) : CoreViewModel() {

    fun createSession(userPhone: String) {
    }
}