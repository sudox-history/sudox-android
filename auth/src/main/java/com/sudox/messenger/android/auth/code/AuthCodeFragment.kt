package com.sudox.messenger.android.auth.code

import android.os.Bundle
import android.view.View
import com.sudox.messenger.android.auth.AuthFragment
import com.sudox.messenger.android.auth.register.AuthRegisterFragment

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