package com.sudox.android.ui.auth

import android.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.data.models.auth.state.AuthSession
import com.sudox.android.ui.auth.common.BaseAuthFragment
import com.sudox.android.ui.auth.confirm.AuthConfirmFragment
import com.sudox.android.ui.auth.phone.AuthPhoneFragment
import com.sudox.android.ui.auth.register.AuthRegisterFragment
import com.sudox.android.ui.main.MainActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.view_navigation_bar.view.*
import javax.inject.Inject

class AuthActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var authViewModel: AuthViewModel
    var authSession: AuthSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Get view model
        authViewModel = getViewModel(viewModelFactory)
        authViewModel.authActivityEventsLiveData.observe(this, Observer {
            if (it == AuthActivityEvent.CONNECTION_CLOSED) {
                showWaitForConnectStatus()
            } else if (it == AuthActivityEvent.HANDSHAKE_SUCCEED) {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentAuthContainer)

                if (currentFragment is BaseAuthFragment) {
                    currentFragment.onConnectionRecovered()
                }
            } else if (it == AuthActivityEvent.ACCOUNT_SESSION_STARTED) {
                showMainActivity()
            } else if(it == AuthActivityEvent.SHOW_OLD_VERSION) {
                AlertDialog.Builder(this)
                        .setTitle(R.string.title_oops)
                        .setMessage(R.string.oops_old_version)
                        .setPositiveButton(R.string.ok) { _, _ -> System.exit(0)}
                        .show()
            }
        })

        // Listen auth session ...
        authViewModel.authActivitySessionLiveData.observe(this, Observer {
            authSession = it!!
            showAuthConfirmFragment()
        })

        showAuthPhoneFragment()

        // Start business logic
        authViewModel.start()
    }

    fun showAuthPhoneFragment(phoneNumber: String? = null, error: String? = null) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentAuthContainer)
        val transaction = supportFragmentManager.beginTransaction()

        if (currentFragment is AuthPhoneFragment) {
            transaction.setCustomAnimations(R.animator.animator_fragment_forward, R.animator.animator_fragment_back_right)
        }

        transaction
                .replace(R.id.fragmentAuthContainer, AuthPhoneFragment().apply {
                    this.phoneNumber = phoneNumber
                    this.error = error
                }).commit()

        // Remove the auth session
        authSession = null
    }

    private fun showAuthConfirmFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentAuthContainer)
        val transaction = supportFragmentManager.beginTransaction()

        if (currentFragment is AuthPhoneFragment) {
            transaction.setCustomAnimations(R.animator.animator_fragment_forward, R.animator.animator_fragment_back_left)
        }

        transaction
                .replace(R.id.fragmentAuthContainer, AuthConfirmFragment())
                .commit()
    }

    fun showAuthRegisterFragment() {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.animator.animator_fragment_change, 0)
                .replace(R.id.fragmentAuthContainer, AuthRegisterFragment())
                .commit()
    }

    private fun showMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()

        // For guarantee that navbar button status is actual
        if (!authViewModel.protocolClient.isValid()) {
            showWaitForConnectStatus()
        }
    }

    private fun showWaitForConnectStatus() {
        authNavigationBar.setClickable(authNavigationBar.buttonNavbarNext, false)
        authNavigationBar.buttonNavbarNext.visibility = View.VISIBLE
        authNavigationBar.buttonNavbarNext.isClickable = false
        authNavigationBar.buttonNavbarNext.setCompoundDrawables(null, null, null, null)
        authNavigationBar.buttonNavbarNext.text = getString(R.string.wait_for_connect)
    }
}