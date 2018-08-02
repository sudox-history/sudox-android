package com.sudox.android.ui.auth.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.sudox.android.R
import com.sudox.android.common.enums.NavigationAction
import com.sudox.android.common.models.dto.AuthSessionDTO
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.auth.confirm.AuthConfirmFragment
import com.sudox.android.ui.auth.confirm.EMAIL_BUNDLE_KEY
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.fragment_auth_email.*
import javax.inject.Inject

class AuthEmailFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var authEmailViewModel: AuthEmailViewModel

    lateinit var email: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        authEmailViewModel = getViewModel(viewModelFactory)

        return inflater.inflate(R.layout.fragment_auth_email, container, false)
    }

    override fun onStart() {
        super.onStart()

        // Configure navigation bar
        authFragmentNavbar
                .navigationLiveData
                .observe(this, Observer<NavigationAction> {
                    if (it == NavigationAction.NEXT) {
                        email = emailEditText.text.toString()
                        authEmailViewModel.sendEmail(email).observe(this, Observer(::getCodeData))
                    }
                })
    }

    private fun getCodeData(it: AuthSessionDTO) {
        when {
            it.errorCode == 3 -> TODO("Invalid format.")
            it.isError() -> TODO("Other error.")
            else -> showAuthCodeFragment(email)
        }
    }

    private fun showAuthCodeFragment(email: String) {
        val bundle = Bundle()

        // Put email to the bundle
        bundle.putString(EMAIL_BUNDLE_KEY, email)

        // Change fragment
        fragmentManager!!.apply {
            beginTransaction()
                    .replace(R.id.fragment_auth_container, AuthConfirmFragment())
                    .commit()
        }
    }

    fun showMessage(message: String){
        Snackbar.make(fragment_auth_container, message, Snackbar.LENGTH_LONG).show()
    }
}