package com.sudox.android.ui.auth

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.sudox.android.R
import com.sudox.android.common.viewmodels.withViewModel
import com.sudox.android.ui.auth.confirm.AuthConfirmFragment
import com.sudox.android.ui.auth.confirm.EMAIL_BUNDLE_KEY
import com.sudox.android.ui.auth.email.AuthEmailFragment
import com.sudox.android.ui.splash.SplashViewModel
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class AuthActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        authViewModel = withViewModel(viewModelFactory){}

        // Start fragment
        supportFragmentManager.apply {
            beginTransaction()
                    .add(R.id.fragment_auth_container, AuthEmailFragment())
                    .commit()
        }
    }

    fun showAuthCodeFragment(email: String) {
        val bundle = Bundle()

        // Put email to the bundle
        bundle.putString(EMAIL_BUNDLE_KEY, email)

        // Change fragment
        supportFragmentManager.apply {
            beginTransaction()
                    .add(R.id.fragment_auth_container, AuthConfirmFragment())
                    .commit()
        }
    }

    override fun onBackPressed() {
        authViewModel.disconnect()
        super.onBackPressed()
    }
}