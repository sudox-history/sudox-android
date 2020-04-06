package ru.sudox.android.auth.inject

import ru.sudox.android.auth.code.AuthCodeScreenVO

interface AuthComponent {
    fun inject(authCodeScreenVO: AuthCodeScreenVO)
}