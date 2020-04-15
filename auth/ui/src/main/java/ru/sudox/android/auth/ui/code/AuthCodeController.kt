package ru.sudox.android.auth.ui.code

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import ru.sudox.android.auth.ui.views.AuthScreenLayout
import ru.sudox.android.core.controllers.ScrollableController

class AuthCodeController : ScrollableController() {

    private var screenVO: AuthCodeScreenVO? = null

    init {
        appBarVO = AuthCodeAppBarVO()
    }

    override fun createChildView(container: ViewGroup, savedViewState: Bundle?): View {
        return AuthScreenLayout(activity!!)
    }

    override fun bindView(view: View) {
        screenVO = AuthCodeScreenVO("79674788147").apply {
            (view as AuthScreenLayout).vo = this
        }
    }
}