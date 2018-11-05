package com.sudox.android.ui.auth.register

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.ViewModelFactory
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.data.models.Errors
import com.sudox.android.data.models.auth.state.AuthSession
import com.sudox.android.data.repositories.auth.AUTH_NAME_REGEX_ERROR
import com.sudox.android.data.repositories.auth.AUTH_NICKNAME_REGEX_ERROR
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.auth.register.enums.AuthRegisterAction
import com.sudox.android.ui.auth.common.BaseAuthFragment
import com.sudox.design.navigation.toolbar.enums.NavigationAction
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.fragment_auth_register.*
import javax.inject.Inject

class AuthRegisterFragment @Inject constructor() : BaseAuthFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var authRegisterViewModel: AuthRegisterViewModel
    lateinit var authActivity: AuthActivity
    lateinit var authSession: AuthSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        authRegisterViewModel = getViewModel(viewModelFactory)
        authActivity = activity as AuthActivity
        authSession = authActivity.authSession!!

        return inflater.inflate(R.layout.fragment_auth_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Слушаем заказанные ViewModel действия ...
        authRegisterViewModel.authRegisterRegexErrorsCallback = {
            nameEditTextContainer.error = null
            nicknameEditTextContainer.error = null

            it.forEach {
                if (it == AUTH_NAME_REGEX_ERROR)  {
                    nameEditTextContainer.error = getString(R.string.wrong_name_format)
                } else if (it == AUTH_NICKNAME_REGEX_ERROR) {
                    nicknameEditTextContainer.error = getString(R.string.wrong_nickname_format)
                }
            }

            unfreeze()
        }

        authRegisterViewModel.authRegisterErrorsLiveData.observe(this, Observer {
            if (it == Errors.INVALID_PARAMETERS) {
                nameEditTextContainer.error = getString(R.string.wrong_name_format)
                nicknameEditTextContainer.error = getString(R.string.wrong_nickname_format)
            } else {
                nameEditTextContainer.error = getString(R.string.unknown_error)
                nicknameEditTextContainer.error = getString(R.string.unknown_error)
            }

            unfreeze()
        })

        authRegisterViewModel.authRegisterActionLiveData.observe(this, Observer {
            when (it) {
                AuthRegisterAction.FREEZE -> freeze()
                AuthRegisterAction.SHOW_EMAIL_FRAGMENT_WITH_CODE_EXPIRED_ERROR -> {
                    authActivity.showAuthEmailFragment(authSession.email, getString(R.string.code_expired))
                }
                AuthRegisterAction.SHOW_EMAIL_FRAGMENT_WITH_INVALID_ACCOUNT_ERROR -> {
                    authActivity.showAuthEmailFragment(authSession.email, getString(R.string.account_is_already_registered))
                }
            }
        })

        // Initializing layout components
        initEditTexts()
        onConnectionRecovered()
    }

    private fun initEditTexts() {
        nameEditText.setText("")
        nicknameEditText.setText("")
    }

    override fun onConnectionRecovered() {
        authActivity.authNavigationBar.reset()
        authActivity.authNavigationBar.nextButtonIsVisible = true
        authActivity.authNavigationBar.sudoxTagIsVisible = false
        authActivity.authNavigationBar.navigationActionCallback = {
            if (it == NavigationAction.NEXT) {
                authRegisterViewModel.signUp(
                        authSession.email,
                        authSession.code!!,
                        authSession.hash,
                        nameEditText.text.toString(),
                        nicknameEditText.text.toString())
            }
        }

        authActivity.authNavigationBar.configureComponents()
    }

    override fun freeze() {
        authActivity.authNavigationBar.freeze()
        nameEditText.isEnabled = false
        nicknameEditText.isEnabled = false
    }

    override fun unfreeze() {
        authActivity.authNavigationBar.unfreeze()
        nameEditText.isEnabled = true
        nicknameEditText.isEnabled = true
    }
}