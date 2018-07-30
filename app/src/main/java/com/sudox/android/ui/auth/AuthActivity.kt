package com.sudox.android.ui.auth

import android.os.Bundle
import com.sudox.android.R
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class AuthActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var authEmailFragment: AuthEmailFragment

    @Inject
    lateinit var authConfirmFragment: AuthConfirmFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Start fragment
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_auth_container, authEmailFragment)
                .commit()
    }

    fun showAuthCodeFragment(email: String) {
        val bundle = Bundle()

        // Put email to the bundle
        bundle.putString(EMAIL_BUNDLE_KEY, email)

        // Change fragment
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_auth_container, authConfirmFragment)
                .commit()
    }
}