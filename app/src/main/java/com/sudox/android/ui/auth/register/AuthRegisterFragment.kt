package com.sudox.android.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.sudox.android.R
import com.sudox.android.common.enums.NavigationAction
import com.sudox.android.common.helpers.NAME_REGEX
import com.sudox.android.common.helpers.hideInputError
import com.sudox.android.common.helpers.showInputError
import com.sudox.android.common.models.dto.SignUpDTO
import com.sudox.android.common.viewmodels.ViewModelFactory
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.auth.confirm.EMAIL_BUNDLE_KEY
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_auth_email.*
import kotlinx.android.synthetic.main.fragment_auth_register.*
import javax.inject.Inject

class AuthRegisterFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var authRegisterViewModel: AuthRegisterViewModel
    lateinit var authActivity: AuthActivity
    lateinit var email: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        authRegisterViewModel = getViewModel(viewModelFactory)
        authActivity = activity as AuthActivity

        return inflater.inflate(R.layout.fragment_auth_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configure view
        val bundle = arguments

        if (bundle != null) {
            val email = bundle.getString(EMAIL_BUNDLE_KEY)

            if (email != null) {
                this.email = email

                // Configure components
                configureNavigationBar()
            } else {
                authActivity.showAuthEmailFragment()
            }
        } else {
            authActivity.showAuthEmailFragment()
        }
    }

    private fun configureNavigationBar() {
        authRegisterFragmentNavbar
                .navigationLiveData
                .observe(this, Observer {
                    if (it == NavigationAction.NEXT) {
                        val name = nameEditText.text
                                .toString()
                                .trim()

                        val surname = surnameEditText.text
                                .toString()
                                .trim()

                        if (NAME_REGEX.matches(name) && NAME_REGEX.matches(surname)) {
                            nameEditText.isEnabled = false
                            surnameEditText.isEnabled = false

                            authRegisterViewModel
                                    .sendUserData(name, surname)
                                    .observe(this, Observer(::getSignUpData))
                        } else {
                            showInputError(nameEditTextContainer)
                            showInputError(surnameEditTextContainer)
//                            nameEditTextContainer.error = getString(R.string.wrong_name_format)
                        }
                    }
                })
    }

    private fun getSignUpData(signUpDTO: SignUpDTO?) {
        if (signUpDTO == null) {
            nameEditText.isEnabled = true
            surnameEditText.isEnabled = true
            hideInputError(nameEditTextContainer)
            hideInputError(surnameEditTextContainer)
            authActivity.showMessage(getString(R.string.no_internet_connection))
        } else if (signUpDTO.errorCode == 3) {
            nameEditText.isEnabled = true
            surnameEditText.isEnabled = true
            showInputError(nameEditTextContainer)
            showInputError(surnameEditTextContainer)
//            nameEditTextContainer.error = getString(R.string.wrong_name_format)
        } else {
            authRegisterViewModel.saveAccount(signUpDTO.id, email, signUpDTO.token)
            authActivity.showMainActivity()
        }
    }
}