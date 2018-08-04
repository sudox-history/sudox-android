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
import com.google.android.material.snackbar.Snackbar
import com.sudox.android.R
import com.sudox.android.common.enums.NavigationAction
import com.sudox.android.common.enums.State
import com.sudox.android.common.models.dto.ConfirmCodeDTO
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.auth.email.AuthEmailFragment
import com.sudox.android.ui.auth.register.AuthRegisterFragment
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.fragment_auth_confirm.*
import kotlinx.android.synthetic.main.include_navbar.*
import javax.inject.Inject



// Email bundle key
internal const val EMAIL_BUNDLE_KEY: String = "email"
internal const val AUTH_STATUS: String = "auth_status"

class AuthConfirmFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var authConfirmViewModel: AuthConfirmViewModel

    lateinit var email: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        authConfirmViewModel = getViewModel(viewModelFactory)

        return inflater.inflate(R.layout.fragment_auth_confirm, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        val data: Bundle = arguments!!
        email = data.getString(EMAIL_BUNDLE_KEY)
        enter_your_code_text.text = "${getString(R.string.enter_code_from_mail)} $email"
        if(data.getInt(AUTH_STATUS) == 0){
            welcome_text.text = getString(R.string.welcome)
        } else {
            welcome_text.text = getString(R.string.return_back)
        }

        authConfirmViewModel.timerData.observe(this, Observer(::setTimerText))
        setupTimer()

        auth_confirm_fragment_navbar
                .navigationLiveData
                .observe(this, Observer {
                    when(it){
                        NavigationAction.BACK -> goToAuthEmailFragment()
                        NavigationAction.SEND_AGAIN -> {
                            authConfirmViewModel.sendCodeAgain().observe(this, Observer(::getResendData))
                            setupTimer()
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

                if(s.toString().length == 5){
                    code_edit_text.isEnabled = false
                    authConfirmViewModel.sendCode(s.toString()).observe(this@AuthConfirmFragment, Observer(::getConfirmData))
                }
            }
        })
    }

    private fun setupTimer(){
        authConfirmViewModel.setTimer(90)
        auth_confirm_fragment_navbar.setClickable(button_navbar_send_again, false)
    }

    private fun finishTimer(){
        auth_confirm_fragment_navbar.setText(button_navbar_send_again, getString(R.string.retry_send))
        auth_confirm_fragment_navbar.setClickable(button_navbar_send_again, true)
    }

    private fun setTimerText(text: String){
        when(text){
            "0" -> finishTimer()
            else -> auth_confirm_fragment_navbar.setText(button_navbar_send_again, text)
        }
    }

    private fun getResendData(data: State){
        when (data){
            State.SUCCESS -> (activity as AuthActivity).showMessage(getString(R.string.code_has_sent_successfully))
            State.FAILED -> (activity as AuthActivity).showMessage(getString(R.string.unknown_error))
        }
    }

    fun getConfirmData(data: ConfirmCodeDTO){
        when(data.codeStatus){
            0 -> {
                code_edit_text.isEnabled = true
                code_edit_text.error = getString(R.string.wrong_code)
            }
            1 -> goToAuthRegisterFragment()
        }
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

    private fun showMessage(message: String){
        Snackbar.make(fragment_auth_container, message, Snackbar.LENGTH_LONG).show()
    }

}