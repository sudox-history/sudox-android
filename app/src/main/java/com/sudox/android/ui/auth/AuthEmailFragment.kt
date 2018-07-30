package com.sudox.android.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sudox.android.ApplicationLoader
import com.sudox.android.R
import com.sudox.android.common.enums.NavigationAction
import com.sudox.android.common.models.dto.AuthSessionDTO
import com.sudox.android.common.viewmodels.getViewModel
import kotlinx.android.synthetic.main.fragment_auth_email.*
import javax.inject.Inject

class AuthEmailFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var authEmailViewModel: AuthEmailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // TODO: Выпилить в пользу AndroidInjector
        (activity?.application as ApplicationLoader).component.inject(this)

        authEmailViewModel = getViewModel(viewModelFactory)

        // Inflate layout
        return inflater.inflate(R.layout.fragment_auth_email, container, false)
    }

    override fun onStart() {
        super.onStart()

        // Configure navigation bar
        authFragmentNavbar
                .navigationLiveData
                .observe(this, Observer<NavigationAction> {
                    if (it == NavigationAction.NEXT) {
                        showAuthCodeFragment()
                    }
                })
    }

    private fun showAuthCodeFragment() {
        val email = emailEditText.text.toString()

        // Send email
        authEmailViewModel
                .sendEmail(email)
                .observe(this, Observer<AuthSessionDTO> {
                    when {
                        it.errorCode == 3 -> TODO("Invalid format.")
                        it.isError() -> TODO("Other error.")
                        else -> (activity as AuthActivity).showAuthCodeFragment(email)
                    }
                })
    }
}