package ru.sudox.android.auth.ui.code

import ru.sudox.android.auth.data.repositories.AuthRepository
import ru.sudox.android.core.CoreViewModel
import javax.inject.Inject

class AuthCodeViewModel @Inject constructor(
        private val authRepository: AuthRepository
) : CoreViewModel() {

    fun checkCode(code: Int) {

    }
}