package com.sudox.android.ui.auth

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.data.models.auth.state.AuthSession
import com.sudox.android.ui.auth.confirm.AuthConfirmFragment
import com.sudox.android.ui.auth.email.AuthEmailFragment
import com.sudox.android.ui.auth.register.AuthRegisterFragment
import com.sudox.android.ui.auth.common.BaseAuthFragment
import com.sudox.android.ui.main.MainActivity
import com.sudox.protocol.models.enums.ConnectionState
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
        authViewModel.connectionStateLiveData.observe(this, Observer {
            if (it == ConnectionState.CONNECTION_CLOSED) {
                showWaitForConnectStatus()
                unfreezeCurrent()
            } else if (it == ConnectionState.HANDSHAKE_SUCCEED) {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentAuthContainer)

                if (currentFragment is BaseAuthFragment) {
                    currentFragment.onConnectionRecovered()
                }
            }
        })

        authViewModel.authSessionStateLiveData.observe(this, Observer {
            if (it?.status != -1) {
                authSession = it!!
                showAuthConfirmFragment()
            }
        })

        authViewModel.accountSessionLiveData.observe(this, Observer {
            if (it?.lived!!) showMainActivity()
        })

        showAuthEmailFragment()
    }

    private fun unfreezeCurrent() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentAuthContainer)
                ?: return

        // Разморозим текущий фрагмент
        if (currentFragment is BaseAuthFragment) {
            currentFragment.unfreeze()
        }
    }

    fun showAuthEmailFragment(email: String? = null, error: String? = null) {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.animator.animator_fragment_change, 0)
                .replace(R.id.fragmentAuthContainer, AuthEmailFragment().apply {
                    this.email = email
                    this.error = error
                }).commit()

        // Remove the auth session
        authSession = null
    }

    private fun showAuthConfirmFragment() {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.animator.animator_fragment_change, 0)
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

        if (!authViewModel.protocolClient.isValid()) {
            showWaitForConnectStatus()
        }
    }

    internal fun showWaitForConnectStatus() {
        authNavigationBar.setClickable(authNavigationBar.buttonNavbarNext, false)
        authNavigationBar.buttonNavbarNext.visibility = View.VISIBLE
        authNavigationBar.buttonNavbarNext.isClickable = false
        authNavigationBar.buttonNavbarNext.setCompoundDrawables(null, null, null, null)
        authNavigationBar.buttonNavbarNext.text = getString(R.string.wait_for_connect)
    }
}