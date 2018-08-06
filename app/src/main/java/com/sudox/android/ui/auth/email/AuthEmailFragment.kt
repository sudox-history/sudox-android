package com.sudox.android.ui.auth.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sudox.android.R
import com.sudox.android.common.enums.NavigationAction
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

    override fun onStart() {
        super.onStart()
        recoveryEmailIfNeeded()
        configureNavigationBar()
    }

    private fun configureNavigationBar() {
        authEmailFragmentNavbar
                .navigationLiveData
                .observe(this, Observer<NavigationAction> {
                    if (it == NavigationAction.NEXT) {
                        email = emailEditText.text
                                .toString()
                                .trim()

                        authEmailViewModel
                                .sendEmail(email)
                                .observe(this, Observer(::getCodeData))
                    }
                })
    }

    private fun recoveryEmailIfNeeded() {
        val data: Bundle? = arguments

        if (data != null) {
            emailEditText.setText(data.getString(EMAIL_BUNDLE_KEY))
        }
    }

    private fun getCodeData(it: AuthSessionDTO?) {
        if (it == null) {
            authActivity.showMessage(getString(R.string.no_internet_connection))
        } else if (it.errorCode == 3) {
            emailEditText.error = getString(R.string.wrong_email_format)
        } else if (it.isError()) {
            emailEditText.error = getString(R.string.unknown_error)
        } else {
            authActivity.hash = it.hash
            authActivity.showAuthCodeFragment(email, it.status)
        }
    }
}