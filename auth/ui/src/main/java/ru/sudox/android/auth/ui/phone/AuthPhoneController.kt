package ru.sudox.android.auth.ui.phone

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import ru.sudox.android.auth.views.AuthScreenLayout
import ru.sudox.android.core.controllers.ScrollableController

class AuthPhoneController : ScrollableController() {

    private val screenVO = AuthPhoneScreenVO()

    override fun createChildView(container: ViewGroup, savedViewState: Bundle?): View {
        return AuthScreenLayout(container.context).apply {
            vo = screenVO
        }
    }
}