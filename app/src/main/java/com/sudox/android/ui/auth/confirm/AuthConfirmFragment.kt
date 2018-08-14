package com.sudox.android.ui.auth.confirm

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sudox.android.R
import com.sudox.android.common.enums.NavigationAction
import com.sudox.android.common.enums.State
import com.sudox.android.common.helpers.formatHtml
import com.sudox.android.common.helpers.hideInputError
import com.sudox.android.common.helpers.showInputError
import com.sudox.android.common.models.dto.ConfirmCodeDTO
import com.sudox.android.common.models.dto.SignInDTO
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.auth.AuthActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_auth_confirm.*
import kotlinx.android.synthetic.main.include_auth_navbar.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// Email bundle key
internal const val EMAIL_BUNDLE_KEY: String = "email"
internal const val AUTH_STATUS_BUNDLE_KEY: String = "auth_status"

class AuthConfirmFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var authConfirmViewModel: AuthConfirmViewModel
    lateinit var authActivity: AuthActivity
    lateinit var email: String
    var authStatus: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        authConfirmViewModel = getViewModel(viewModelFactory)
        authActivity = activity as AuthActivity

        return inflater.inflate(R.layout.fragment_auth_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configure view
        val bundle = arguments

        if (bundle != null) {
            val email = bundle.getString(EMAIL_BUNDLE_KEY)
            val authStatus = bundle.getInt(AUTH_STATUS_BUNDLE_KEY)

            if (email != null) {
                // Write email as the fragment variable
                this.email = email
                this.authStatus = authStatus

                initWelcomeText()
                initCodeEditText()
                initFooterText()
                initNavigationBar()
                initTimer()
            } else {
                authActivity.showAuthEmailFragment()
            }
        } else {
            authActivity.showAuthEmailFragment()
        }
    }

    private fun initTimer() {
        authConfirmViewModel
                .timerData
                .observe(this, Observer(::setTimerText))

        setupTimer()
    }

    private fun initFooterText() {
        enterYourCodeText.text = formatHtml(
                getString(R.string.enter_code_from_mail, email)
        )
    }

    private fun initWelcomeText() {
        if (authStatus == 0) {
            welcomeText.text = getString(R.string.welcome)
        } else {
            welcomeText.text = getString(R.string.return_back)
        }
    }

    private fun initCodeEditText() {
        codeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(chars: CharSequence, start: Int, before: Int, count: Int) {
                val text = chars.toString()

                if (codeEditTextContainer.isErrorEnabled) {
                    hideInputError(codeEditTextContainer)
                }

                if (text.length == 5) {
                    codeEditText.isEnabled = false

                    if (authStatus == 0) {
                        authConfirmViewModel
                                .sendCode(text)
                                .observe(this@AuthConfirmFragment, Observer(::getConfirmData))
                    } else {
                        authConfirmViewModel
                                .signIn(text)
                                .observe(this@AuthConfirmFragment, Observer(::getSignInData))
                    }
                }
            }
        })
    }

    private fun initNavigationBar() {
        authConfirmFragmentNavbar
                .navigationLiveData
                .observe(this, Observer {
                    if (it == NavigationAction.BACK) {
                        authActivity.showAuthEmailFragment(email)
                    } else if (it == NavigationAction.SEND_AGAIN) {
                        authConfirmViewModel
                                .sendCodeAgain()
                                .observe(this, Observer(::getResendData))
                    }
                })
    }

    private fun setupTimer() {
        authConfirmViewModel.setTimer(95)
        authConfirmFragmentNavbar.setClickable(buttonSomeFeature, false)
    }

    private fun finishTimer() {
        authConfirmFragmentNavbar.setText(buttonSomeFeature, getString(R.string.retry_send))
        authConfirmFragmentNavbar.setClickable(buttonSomeFeature, true)
    }

    private fun setTimerText(seconds: Long) {
        if (seconds == 0L) {
            finishTimer()
        } else {
            val text = getString(R.string.retry_send_in)
                    .replace("%time", formatTimeToEnd(seconds))

            authConfirmFragmentNavbar.setText(buttonSomeFeature, text)
        }
    }

    private fun getResendData(data: State?) {
        when (data) {
            null -> authActivity.showMessage(getString(R.string.no_internet_connection))
            State.SUCCESS -> {
                authActivity.showMessage(getString(R.string.code_has_sent_successfully))
                setupTimer()
            }
            State.FAILED -> authActivity.showMessage(getString(R.string.unknown_error))
        }
    }

    fun getConfirmData(data: ConfirmCodeDTO?) {
        when {
            data == null -> {
                authActivity.showMessage(getString(R.string.no_internet_connection))
                hideInputError(codeEditTextContainer)
                codeEditText.isEnabled = true
            }
            data.codeStatus == 0 -> {
//            codeEditTextContainer.error = getString(R.string.wrong_code)
                showInputError(codeEditTextContainer)
                codeEditText.isEnabled = true
            }
            data.codeStatus == 1 -> authActivity.showAuthRegisterFragment(email)
        }
    }

    private fun getSignInData(signInDTO: SignInDTO?) {
        if (signInDTO == null) {
            authActivity.showMessage(getString(R.string.no_internet_connection))
            hideInputError(codeEditTextContainer)
            codeEditText.isEnabled = true
        } else if (signInDTO.status == 0) {
//            codeEditTextContainer.error = getString(R.string.wrong_code)
            showInputError(codeEditTextContainer)
            codeEditText.isEnabled = true
        } else {
            authConfirmViewModel.saveAccount(signInDTO.id, email, signInDTO.token)
            authActivity.showMainActivity()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatTimeToEnd(second: Long): String {
        val format = SimpleDateFormat("mm:ss").apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        return format.format(Date(second * 1000))
    }
}