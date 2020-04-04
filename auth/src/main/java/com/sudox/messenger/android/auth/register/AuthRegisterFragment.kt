package com.sudox.messenger.android.auth.register

import com.sudox.messenger.android.auth.AuthFragment

class AuthRegisterFragment : AuthFragment<AuthRegisterScreenVO>() {

    init {
        appBarVO = AuthRegisterAppBarVO()
        screenVO = AuthRegisterScreenVO()
    }
}