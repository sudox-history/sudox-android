package com.sudox.android.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.sudox.android.R
import com.sudox.android.common.enums.NavigationAction
import com.sudox.android.common.models.dto.SignUpDTO
import com.sudox.android.common.viewmodels.ViewModelFactory
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.MainActivity
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.auth.confirm.EMAIL_BUNDLE_KEY
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_register.*
import javax.inject.Inject

class AuthRegisterFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var authRegisterViewModel: AuthRegisterViewModel

    lateinit var authActivity: AuthActivity

    lateinit var email: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        authRegisterViewModel = getViewModel(viewModelFactory)

        authActivity = activity as AuthActivity

        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        email = arguments!!.getString(EMAIL_BUNDLE_KEY)

        auth_register_fragment_navbar
                .navigationLiveData
                .observe(this, Observer<NavigationAction> {
                    when (it) {
                        NavigationAction.NEXT -> authRegisterViewModel
                                .sendUserData(name_edit_text.text.toString(),
                                        surname_edit_text.text.toString()).observe(this, Observer(::getSignUpData))
                    }
                })
    }

    private fun getSignUpData(signUpDTO: SignUpDTO?) {
        when {
            signUpDTO == null -> authActivity.showMessage(getString(R.string.no_internet_connection))
            signUpDTO.errorCode == 3 -> name_edit_text.error = getString(R.string.wrong_name_format)
            else -> {
                authRegisterViewModel.saveAccount(signUpDTO.id, email, signUpDTO.token)
                authActivity.goToMainActivity()
            }
        }
    }
}