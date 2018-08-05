package com.sudox.android.ui.auth.confirm

import android.annotation.SuppressLint
import android.content.Intent
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
import com.sudox.android.common.models.dto.ConfirmCodeDTO
import com.sudox.android.common.models.dto.SignInDTO
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.MainActivity
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.auth.email.AuthEmailFragment
import com.sudox.android.ui.auth.register.AuthRegisterFragment
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_auth_confirm.*
import kotlinx.android.synthetic.main.include_navbar.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


// Email bundle key
internal const val EMAIL_BUNDLE_KEY: String = "email"
internal const val AUTH_STATUS: String = "auth_status"

class AuthConfirmFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var authConfirmViewModel: AuthConfirmViewModel

    lateinit var email: String
    var authStatus: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        authConfirmViewModel = getViewModel(viewModelFactory)

        return inflater.inflate(R.layout.fragment_auth_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data: Bundle = arguments!!

        email = data.getString(EMAIL_BUNDLE_KEY)
        enter_your_code_text.text = "${getString(R.string.enter_code_from_mail)} $email"

        authStatus = data.getInt(AUTH_STATUS)

        if (authStatus == 0) {
            welcome_text.text = getString(R.string.welcome)
        } else {
            welcome_text.text = getString(R.string.return_back)
        }

        authConfirmViewModel.timerData.observe(this, Observer(::setTimerText))
        setupTimer()

        auth_confirm_fragment_navbar
                .navigationLiveData
                .observe(this, Observer {
                    when (it) {
                        NavigationAction.BACK -> goToAuthEmailFragment()
                        NavigationAction.SEND_AGAIN -> {
                            authConfirmViewModel.sendCodeAgain().observe(this, Observer(::getResendData))

                        }
                    }
                })

        code_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

                if (s.toString().length == 5) {
                    code_edit_text.isEnabled = false
                    if(authStatus == 0) {
                        authConfirmViewModel.sendCode(s.toString())
                                .observe(this@AuthConfirmFragment, Observer(::getConfirmData))
                    }
                    else{
                        authConfirmViewModel.signIn(s.toString())
                                .observe(this@AuthConfirmFragment, Observer(::getSignInData))
                    }
                }
            }
        })
    }

    private fun setupTimer() {
        authConfirmViewModel.setTimer(95)
        auth_confirm_fragment_navbar.setClickable(buttonSomeFeature, false)
    }

    private fun finishTimer() {
        auth_confirm_fragment_navbar.setText(buttonSomeFeature, getString(R.string.retry_send))
        auth_confirm_fragment_navbar.setClickable(buttonSomeFeature, true)
    }

    private fun setTimerText(seconds: Long) {
        when (seconds) {
            0L -> finishTimer()
            else -> auth_confirm_fragment_navbar.setText(buttonSomeFeature,
                    "${getString(R.string.retry_send_in)} ${formatTimeToEnd(seconds)}")
        }
    }

    private fun getResendData(data: State) {
        when (data) {
            State.SUCCESS -> {
                (activity as AuthActivity).showMessage(getString(R.string.code_has_sent_successfully))
                setupTimer()
            }
            State.FAILED -> (activity as AuthActivity).showMessage(getString(R.string.unknown_error))
        }
    }

    fun getConfirmData(data: ConfirmCodeDTO) {
        when (data.codeStatus) {
            0 -> {
                code_edit_text.isEnabled = true
                code_edit_text.error = getString(R.string.wrong_code)
            }
            1 -> goToAuthRegisterFragment()
        }
    }

    private fun getSignInData(signInDTO: SignInDTO) {
        when{
            signInDTO.status == 0 -> code_edit_text.error = getString(R.string.wrong_code)
            else -> {
                authConfirmViewModel.saveAccount(signInDTO.id, signInDTO.token)
                goToMainActivity()
            }

        }

    }

    private fun goToMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        activity!!.finish()
    }

    private fun goToAuthRegisterFragment() {
        fragmentManager!!.apply {
            beginTransaction()
                    .replace(R.id.fragment_auth_container, AuthRegisterFragment())
                    .commit()
        }
    }

    private fun goToAuthEmailFragment() {
        val bundle = Bundle()

        bundle.putString(EMAIL_BUNDLE_KEY, email)

        fragmentManager!!.apply {
            beginTransaction()
                    .replace(R.id.fragment_auth_container, AuthEmailFragment().apply {
                        arguments = bundle
                    })
                    .commit()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun formatTimeToEnd(second: Long): String {
        val format = SimpleDateFormat("mm:ss")
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(Date(second * 1000))
    }

}