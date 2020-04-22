package ru.sudox.android.auth.ui.verify

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import ru.sudox.android.core.controllers.ScrollableController

class AuthVerifyController : ScrollableController() {

    init {
        appBarVO = AuthVerifyAppBarVO()
    }

    override fun createChildView(container: ViewGroup, savedViewState: Bundle?): View {
        return AuthVerifyLayout(activity!!)
    }
}