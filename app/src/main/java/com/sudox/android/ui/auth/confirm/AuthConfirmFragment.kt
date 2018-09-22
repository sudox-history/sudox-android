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
import com.sudox.android.ui.views.enums.NavigationAction
import com.sudox.android.common.helpers.formatHtml
import com.sudox.android.common.helpers.hideInputError
import com.sudox.android.common.helpers.showInputError
import com.sudox.android.common.models.auth.state.AuthSession
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.auth.confirm.enums.AuthConfirmAction
import com.sudox.android.ui.common.FreezableFragment
import kotlinx.android.synthetic.main.fragment_auth_confirm.*
import javax.inject.Inject

class AuthConfirmFragment @Inject constructor() : FreezableFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var authConfirmViewModel: AuthConfirmViewModel
    lateinit var authActivity: AuthActivity
    lateinit var authSession: AuthSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        authConfirmViewModel = getViewModel(viewModelFactory)
        authActivity = activity as AuthActivity
        authSession = authActivity.authSession

        return inflater.inflate(R.layout.fragment_auth_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle actions requests ...
        authConfirmViewModel.authConfirmActionLiveData.observe(this, Observer {
            when (it) {
                AuthConfirmAction.FREEZE -> freeze()
                AuthConfirmAction.SHOW_REGISTRATION -> authActivity.showAuthRegisterFragment()
                AuthConfirmAction.SHOW_ERROR -> {
                    showInputError(codeEditTextContainer)
                    unfreeze()
                }
            }
        })

        // Init layout components
        initWelcomeText()
        initFooterText()
        initCodeEditText()
        initNavigationBar()
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

        // Автоматический сброс ошибки при вводе и автоматическая отправка кода при достижении длины 5 символов
        codeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(chars: CharSequence, start: Int, before: Int, count: Int) {
                if (codeEditTextContainer.isErrorEnabled) {
                    hideInputError(codeEditTextContainer)
                }

                if (count + start == codeLength) {
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
            }
        })
    }

    private fun initNavigationBar() {
        authConfirmFragmentNavbar.navigationActionCallback = {
            if (it == NavigationAction.BACK) {
                // Clear data
                codeEditText.text = null

                // Change fragment
                authActivity.showAuthEmailFragment(authSession.email)
            } else if (it == NavigationAction.SOME_FEATURE) {
                // TODO: Try send code again
            }
        }
    }

    override fun freeze() {
        codeEditText.isEnabled = false

        // Freeze navbar
        authConfirmFragmentNavbar.freeze()
    }

    override fun unfreeze() {
        codeEditText.isEnabled = true

        // Unfreeze navbar
        authConfirmFragmentNavbar.unfreeze()
    }
}