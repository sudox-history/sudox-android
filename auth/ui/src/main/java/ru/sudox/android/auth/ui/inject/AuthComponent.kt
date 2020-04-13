package ru.sudox.android.auth.ui.inject

import ru.sudox.android.auth.ui.code.AuthCodeScreenVO

interface AuthUiComponent {
    fun inject(authCodeScreenVO: AuthCodeScreenVO)
}