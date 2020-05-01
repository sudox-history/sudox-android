package ru.sudox.android.auth.ui.phone

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import ru.sudox.android.auth.data.entities.AuthSessionStage
import ru.sudox.android.auth.ui.code.AuthCodeController
import ru.sudox.android.auth.ui.signup.AuthSignUpController
import ru.sudox.android.auth.ui.views.AuthScreenLayout
import ru.sudox.android.core.controllers.ScrollableController
import ru.sudox.android.countries.COUNTRY_CHANGE_REQUEST_CODE
import ru.sudox.android.countries.COUNTRY_EXTRA_NAME
import ru.sudox.android.countries.CountrySelectController
import ru.sudox.api.common.getErrorText

class AuthPhoneController : ScrollableController() {

    private var viewModel: AuthPhoneViewModel? = null
    private var screenVO: AuthPhoneScreenVO? = null

    init {
        appBarVO = AuthPhoneAppBarVO()
    }

    override fun createChildView(container: ViewGroup, savedViewState: Bundle?): View {
        return AuthScreenLayout(container.context)
    }

    override fun bindView(view: View) {
        super.bindView(view)

        screenVO = AuthPhoneScreenVO().apply {
            (view as AuthScreenLayout).vo = this

            phoneEditText!!.countrySelector.setOnClickListener {
                navigationManager!!.showRootChild(CountrySelectController().apply {
                    targetController = this@AuthPhoneController
                })
            }
        }

        viewModel = getViewModel()
        viewModel!!.apply {
            successLiveData.observe(this@AuthPhoneController, Observer {
                if (it != null) {
                    if (it.stage == AuthSessionStage.PHONE_CHECKED) {
                        navigationManager!!.showRootChild(AuthCodeController())
                    } else if (it.stage == AuthSessionStage.CODE_CHECKED) {
                        if (!it.userExists) {
                            navigationManager!!.showRootChild(AuthSignUpController())
                        }
                    }
                }
            })

            errorsLiveData.observe(this@AuthPhoneController, Observer {
                screenVO!!.phoneEditTextLayout!!.errorText = if (it != null) {
                    getErrorText(it)
                } else {
                    null
                }
            })

            loadingStateLiveData.observe(this@AuthPhoneController, Observer {
                appBarManager!!.toggleLoading(it)
                screenVO!!.phoneEditTextLayout!!.isEnabled = !it
            })
        }
    }

    override fun onAppBarClicked(tag: Int) {
        super.onAppBarClicked(tag)

        if (tag == AUTH_PHONE_NEXT_BUTTON_TAG) {
            viewModel!!.createSession(screenVO!!.phoneEditText!!.phoneNumber!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == COUNTRY_CHANGE_REQUEST_CODE) {
            screenVO!!.phoneEditText!!.vo = data!!.getParcelableExtra(COUNTRY_EXTRA_NAME)
        }
    }
}