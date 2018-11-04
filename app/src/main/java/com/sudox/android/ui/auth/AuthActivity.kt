package com.sudox.android.ui.auth

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentTransaction
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.design.helpers.showSnackbar
import com.sudox.android.data.models.auth.state.AuthSession
import com.sudox.android.ui.auth.confirm.AuthConfirmFragment
import com.sudox.android.ui.auth.email.AuthEmailFragment
import com.sudox.android.ui.auth.register.AuthRegisterFragment
import com.sudox.android.ui.common.FreezableFragment
import com.sudox.android.ui.main.MainActivity
import com.sudox.protocol.models.enums.ConnectionState
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_auth.*
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
            if (it == ConnectionState.CONNECTION_CLOSED || it == ConnectionState.CONNECTION_CLOSED) {
                if (authSession != null) {
                    showAuthEmailFragment(authSession!!.email)
                } else {
                    unfreezeCurrent()
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

        showAuthEmailFragment(null, true)
    }

    private fun unfreezeCurrent() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentAuthContainer)
                ?: return

        // Разморозим текущий фрагмент
        if (currentFragment is FreezableFragment) {
            currentFragment.unfreeze()
        }
    }

    fun showAuthEmailFragment(email: String? = null, isFirstStart: Boolean = false) {
        val authEmailFragment = AuthEmailFragment().apply { this.email = email }
        val transaction = supportFragmentManager.beginTransaction()

        if (isFirstStart) {
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        } else {
            transaction.setCustomAnimations(R.animator.animator_fragment_change, 0)
        }

        transaction.replace(R.id.fragmentAuthContainer, authEmailFragment)
        transaction.commit()

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

    fun showMessage(message: String) = showSnackbar(this, fragmentAuthContainer, message, Snackbar.LENGTH_LONG)
}