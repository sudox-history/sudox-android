package ru.sudox.android.auth.ui.signup

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import ru.sudox.android.auth.ui.phone.AUTH_PHONE_NEXT_BUTTON_TAG
import ru.sudox.android.auth.ui.views.AuthScreenLayout
import ru.sudox.android.core.controllers.ScrollableController
import ru.sudox.android.core.exceptions.InvalidFieldFormatException
import ru.sudox.android.core.managers.MAIN_ROOT_TAG
import ru.sudox.api.common.getErrorText

class AuthSignUpController : ScrollableController() {

    private var screenVO: AuthSignUpScreenVO? = null
    private var viewModel: AuthSignUpViewModel? = null

    init {
        appBarVO = AuthSignUpAppBarVO()
    }

    override fun createChildView(container: ViewGroup, savedViewState: Bundle?): View {
        return AuthScreenLayout(activity!!)
    }

    override fun bindView(view: View) {
        super.bindView(view)

        screenVO = AuthSignUpScreenVO().apply {
            (view as AuthScreenLayout).vo = this
        }

        viewModel = getViewModel()
        viewModel!!.errorsLiveData.observe(this, Observer {
            if (it is InvalidFieldFormatException) {
                if (it.fields.contains(0)) {
                    screenVO!!.nameEditTextLayout!!.errorText = "Invalid name format"
                } else {
                    screenVO!!.nameEditTextLayout!!.errorText = null
                }

                if (it.fields.contains(1)) {
                    screenVO!!.nicknameEditTextLayout!!.errorText = "Invalid nickname format"
                } else {
                    screenVO!!.nicknameEditTextLayout!!.errorText = null
                }
            } else if (it != null) {
                screenVO!!.nameEditTextLayout!!.errorText = getErrorText(it)
                screenVO!!.nicknameEditTextLayout!!.errorText = getErrorText(it)
            }
        })

        viewModel!!.loadingStateLiveData.observe(this, Observer {
            appBarManager!!.toggleLoading(it)
            screenVO!!.nameEditTextLayout!!.isEnabled = !it
            screenVO!!.nicknameEditTextLayout!!.isEnabled = !it
        })

        viewModel!!.successLiveData.observe(this, Observer {
            navigationManager!!.showRoot(MAIN_ROOT_TAG)
        })
    }


    override fun onAppBarClicked(tag: Int) {
        super.onAppBarClicked(tag)

        if (tag == AUTH_SIGN_UP_FINISH_BUTTON_TAG) {
            val name = screenVO!!.nameEditText!!.text.toString()
            val nickname = screenVO!!.nicknameEditText!!.text.toString()

            viewModel!!.signUp(name, nickname)
        }
    }
}