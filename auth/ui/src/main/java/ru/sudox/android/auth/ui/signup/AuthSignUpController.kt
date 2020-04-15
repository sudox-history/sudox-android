package ru.sudox.android.auth.ui.signup

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import ru.sudox.android.auth.ui.views.AuthScreenLayout
import ru.sudox.android.core.controllers.ScrollableController

class AuthSignUpController : ScrollableController() {

    private var screenVO: AuthSignUpScreenVO? = null

    init {
        appBarVO = AuthSignUpAppBarVO()
    }

    override fun createChildView(container: ViewGroup, savedViewState: Bundle?): View {
        return AuthScreenLayout(activity!!)
    }

    override fun bindView(view: View) {
        screenVO = AuthSignUpScreenVO().apply {
            (view as AuthScreenLayout).vo = this
        }
    }
}