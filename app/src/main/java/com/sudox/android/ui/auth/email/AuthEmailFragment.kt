package com.sudox.android.ui.auth.email

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
import com.sudox.android.common.helpers.EMAIL_REGEX
import com.sudox.android.common.helpers.hideInputError
import com.sudox.android.common.helpers.showInputError
import com.sudox.android.common.models.dto.AuthSessionDTO
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.auth.confirm.EMAIL_BUNDLE_KEY
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_auth_email.*
import javax.inject.Inject

class AuthEmailFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var authEmailViewModel: AuthEmailViewModel
    lateinit var authActivity: AuthActivity
    lateinit var email: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        authEmailViewModel = getViewModel(viewModelFactory)
        authActivity = activity as AuthActivity

        return inflater.inflate(R.layout.fragment_auth_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recoveryEmailIfNeeded()
        initEmailEditText()
        initNavigationBar()
    }

    private fun initEmailEditText() {
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (emailEditTextContainer.isErrorEnabled) {
                    hideInputError(emailEditTextContainer)
                }
            }
        })
    }

    private fun initNavigationBar() {
        authEmailFragmentNavbar
                .navigationLiveData
                .observe(this, Observer<NavigationAction> {
                    if (it == NavigationAction.NEXT) {
                        email = emailEditText.text
                                .toString()
                                .trim()

                        if (EMAIL_REGEX.matches(email)) {
                            emailEditText.isEnabled = false

                            authEmailViewModel
                                    .sendEmail(email)
                                    .observe(this, Observer(::getCodeData))
                        } else {
                            showInputError(emailEditTextContainer)
                        }
                    }
                })
    }

    private fun recoveryEmailIfNeeded() {
        val bundle = arguments

        if (bundle != null) {
            val email = bundle.getString(EMAIL_BUNDLE_KEY)

            if (email != null) {
                emailEditText.setText(email)
            }
        }
    }

    private fun getCodeData(it: AuthSessionDTO?) {
        if (it == null) {
            emailEditText.isEnabled = true
            hideInputError(emailEditTextContainer)
            authActivity.showMessage(getString(R.string.no_internet_connection))
        } else if (it.errorCode == 3) {
            emailEditText.isEnabled = true
            showInputError(emailEditTextContainer)
        } else if (it.isError()) {
            emailEditText.isEnabled = true
            showInputError(emailEditTextContainer)
        } else {
            authActivity.hash = it.hash
            authActivity.showAuthCodeFragment(email, it.status)
        }
    }
}