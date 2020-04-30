package ru.sudox.android.auth.ui.code

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import ru.sudox.android.auth.ui.signup.AuthSignUpController
import ru.sudox.android.auth.ui.verify.AuthVerifyController
import ru.sudox.android.auth.ui.views.AuthScreenLayout
import ru.sudox.android.core.controllers.ScrollableController
import ru.sudox.api.common.getErrorText

class AuthCodeController : ScrollableController() {

    private var viewModel: AuthCodeViewModel? = null
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

        viewModel = getViewModel()
        viewModel!!.sessionDestroyedLiveData.observe(this, Observer {
            navigationManager!!.popBackstack()
        })

        viewModel!!.successLiveData.observe(this, Observer {
            if (it != null) {
                if (it) {
                    navigationManager!!.showRootChild(AuthVerifyController(), true)
                } else {
                    navigationManager!!.showRootChild(AuthSignUpController(), true)
                }
            }
        })

        viewModel!!.loadingStateLiveData.observe(this, Observer {
            appBarManager!!.toggleLoading(it)
            screenVO!!.codeEditTextLayout!!.isEnabled = !it
        })

        viewModel!!.errorsLiveData.observe(this, Observer {
            screenVO!!.codeEditTextLayout!!.errorText = if (it != null) {
                getErrorText(it)
            } else {
                null
            }
        })

        screenVO!!.codeEditText!!.codeFilledCallback = {
            viewModel!!.checkCode(it.toInt())
        }
    }
}