package com.sudox.android.ui.auth.confirm

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.data.models.Errors
import com.sudox.android.data.models.auth.state.AuthSession
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.auth.common.BaseAuthFragment
import com.sudox.android.ui.auth.confirm.enums.AuthConfirmAction
import com.sudox.design.helpers.formatHtml
import com.sudox.design.navigation.toolbar.enums.NavigationAction
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.fragment_auth_confirm.*
import javax.inject.Inject

class AuthConfirmFragment @Inject constructor() : BaseAuthFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var authConfirmViewModel: AuthConfirmViewModel
    lateinit var authActivity: AuthActivity
    lateinit var authSession: AuthSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        authConfirmViewModel = getViewModel(viewModelFactory)
        authActivity = activity as AuthActivity
        authSession = authActivity.authSession!!

        return inflater.inflate(R.layout.fragment_auth_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle actions requests ...
        authConfirmViewModel.authConfirmErrorsLiveData.observe(this, Observer {
            codeEditTextContainer.error = if (it == Errors.WRONG_CODE) {
                getString(R.string.wrong_code)
            } else  {
                getString(R.string.unknown_error)
            }

            unfreeze()
        })

        authConfirmViewModel.authConfirmActionLiveData.observe(this, Observer {
            when (it) {
                AuthConfirmAction.FREEZE -> freeze()
                AuthConfirmAction.SHOW_REGISTER_FRAGMENT -> authActivity.showAuthRegisterFragment()
                AuthConfirmAction.SHOW_EMAIL_FRAGMENT_WITH_CODE_EXPIRED_ERROR -> {
                    authActivity.showAuthEmailFragment(authSession.email, getString(R.string.code_expired))
                }
                AuthConfirmAction.SHOW_EMAIL_FRAGMENT_WITH_TOO_MANY_REQUESTS -> {
                    codeEditTextContainer.error = getString(R.string.too_many_requests)
                    unfreeze()
                }
            }
        })

        // Init layout components
        initWelcomeText()
        initFooterText()
        initCodeEditText()
        onConnectionRecovered()
    }

    private fun initWelcomeText() {
        welcomeText.text = getString(if (authSession.status == AuthSession.AUTH_STATUS_REGISTERED) {
            R.string.return_back
        } else {
            R.string.welcome
        })
    }

    private fun initFooterText() {
        enterYourCodeText.text = formatHtml(getString(R.string.enter_code_from_mail, authSession.email))
    }

    private fun initCodeEditText() {
        val codeLength = resources.getInteger(R.integer.length_email_code)

        // Чистим поле ввода
        codeEditText.setText("")
        codeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(chars: CharSequence, start: Int, before: Int, count: Int) {
                if (count + start == codeLength) {
                    sendCode()
                }
            }
        })
    }

    private fun sendCode() {
        val code = codeEditText.text.toString()

        // Начинаем отправку, блокируем ввод кода ...
        if (authSession.status == AuthSession.AUTH_STATUS_NOT_REGISTERED) {
            authConfirmViewModel.checkCode(authSession.email, code, authSession.hash)
        } else {
            authConfirmViewModel.signIn(authSession.email, code, authSession.hash)
        }

        // Сохраним код ... (похер если вылезет ошибка, код в таких случаях перезапишется по-любому)
        authSession.code = code
    }

    override fun onConnectionRecovered() {
        authActivity.authNavigationBar.reset()
        authActivity.authNavigationBar.backButtonIsVisible = true
        authActivity.authNavigationBar.someFeatureButtonIsVisible = false
        authActivity.authNavigationBar.navigationActionCallback = {
            if (it == NavigationAction.BACK) {
                codeEditText.setText("")
                authActivity.showAuthEmailFragment(authSession.email)
            } else if (it == NavigationAction.SOME_FEATURE) {
                // TODO: Try send code again
            }
        }

        authActivity.authNavigationBar.configureComponents()

        // Send code
        if (codeEditText.text.length == resources.getInteger(R.integer.length_email_code)) {
            sendCode()
        }
    }

    override fun freeze() {
        codeEditText.isEnabled = false
        authActivity.authNavigationBar.freeze()
    }

    override fun unfreeze() {
        codeEditText.isEnabled = true
        authActivity.authNavigationBar.unfreeze()
    }
}