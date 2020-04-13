package ru.sudox.android.auth.ui.code

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import ru.sudox.android.auth.ui.views.AuthScreenLayout
import ru.sudox.android.core.controllers.ScrollableController

class AuthCodeController : ScrollableController() {

    private val screenVO = AuthCodeScreenVO("79674788147")

    init {
        appBarVO = AuthCodeAppBarVO()
    }

    override fun createChildView(container: ViewGroup, savedViewState: Bundle?): View {
        return AuthScreenLayout(activity!!).apply {
            vo = screenVO
        }
    }

    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        super.onChangeStarted(changeHandler, changeType)

        if (changeType.isEnter) {
            screenVO.codeEditText!!.codeFilledCallback = {
//                navigationManager!!.showChildFragment(AuthRegisterFragment())
            }
        }
    }
}