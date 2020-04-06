package ru.sudox.android.auth.register

import ru.sudox.android.auth.AuthFragment

class AuthRegisterFragment : AuthFragment<AuthRegisterScreenVO>() {

    init {
        appBarVO = AuthRegisterAppBarVO()
        screenVO = AuthRegisterScreenVO()
    }
}