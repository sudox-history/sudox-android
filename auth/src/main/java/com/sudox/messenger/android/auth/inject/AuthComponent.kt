package com.sudox.messenger.android.auth.inject

import com.sudox.messenger.android.auth.code.AuthCodeScreenVO

interface AuthComponent {
    fun inject(authCodeScreenVO: AuthCodeScreenVO)
}