package ru.sudox.android.auth.code

import android.os.Bundle
import android.view.View
import ru.sudox.android.auth.AuthFragment
import ru.sudox.android.auth.register.AuthRegisterFragment

class AuthCodeFragment : AuthFragment<AuthCodeScreenVO>() {

    init {
        appBarVO = AuthCodeAppBarVO()
        screenVO = AuthCodeScreenVO("79674788147")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        screenVO!!.codeEditText!!.codeFilledCallback = {
            navigationManager!!.showChildFragment(AuthRegisterFragment())
        }
    }
}