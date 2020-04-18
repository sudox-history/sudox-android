package ru.sudox.android.auth.ui.code

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import ru.sudox.android.auth.ui.signup.AuthSignUpController
import ru.sudox.android.auth.ui.views.AuthScreenLayout
import ru.sudox.android.core.controllers.ScrollableController
import ru.sudox.api.getErrorText

class AuthCodeController : ScrollableController() {

    private var authCodeViewModel: AuthCodeViewModel? = null
    private var screenVO: AuthCodeScreenVO? = null

    init {
        appBarVO = AuthCodeAppBarVO()
    }

    override fun createChildView(container: ViewGroup, savedViewState: Bundle?): View {
        return AuthScreenLayout(activity!!)
    }

    override fun bindView(view: View) {
        super.bindView(view)
        screenVO = AuthCodeScreenVO("79674788147").apply {
            (view as AuthScreenLayout).vo = this
        }

        authCodeViewModel = getViewModel()

        authCodeViewModel!!.loadingLiveData.observe(this, Observer {
            screenVO!!.codeEditTextLayout!!.isEnabled = !it
        })

        authCodeViewModel!!.errorsLiveData.observe(this, Observer {
            if (it != null) {
                screenVO!!.codeEditTextLayout!!.errorText = getErrorText(activity!!, it)
            } else {
                screenVO!!.codeEditTextLayout!!.errorText = null
            }
        })

        authCodeViewModel!!.successLiveData.observe(this, Observer {
            navigationManager!!.showRootChild(AuthSignUpController())
        })

        screenVO!!.codeEditText!!.codeFilledCallback = {
            authCodeViewModel!!.checkCode(it.toInt())
        }
    }
}