package com.sudox.android.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.sudox.android.R
import com.sudox.android.common.enums.AuthHashState
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.enums.State
import com.sudox.android.common.helpers.hideInputError
import com.sudox.android.common.helpers.showSnackbar
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.auth.confirm.AUTH_STATUS_BUNDLE_KEY
import com.sudox.android.ui.auth.confirm.AuthConfirmFragment
import com.sudox.android.ui.auth.confirm.EMAIL_BUNDLE_KEY
import com.sudox.android.ui.auth.email.AuthEmailFragment
import com.sudox.android.ui.auth.register.AuthRegisterFragment
import com.sudox.android.ui.main.MainActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.fragment_auth_confirm.*
import javax.inject.Inject

class AuthActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var authViewModel: AuthViewModel

    var hash: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        authViewModel = getViewModel(viewModelFactory)
        authViewModel.connectLiveData.observe(this, Observer(::getConnectState))

        showStartFragment(false)
    }

    private fun getConnectState(connectState: ConnectState?) {
        if (connectState == ConnectState.RECONNECTED) {
            showMessage(getString(R.string.connection_restored))

            if (codeEditTextContainer != null && hash != null) {
                authViewModel
                        .importAuthHash(hash!!)
                        .observe(this, Observer(::getHashData))
            }
        } else if (connectState == ConnectState.CONNECT_ERROR) {
            showMessage(getString(R.string.lost_internet_connection))
        }
    }

    private fun getHashData(data: AuthHashState) {
        when (data) {
            AuthHashState.DEAD -> showStartFragment(true)
            State.FAILED -> {
                showMessage(getString(R.string.unknown_error))
                hideInputError(codeEditTextContainer)
            }
            AuthHashState.AUTH_HASH_ERROR -> {
                showMessage(getString(R.string.unknown_error))
                hideInputError(codeEditTextContainer)
            }
        }
    }

    fun showAuthEmailFragment(email: String? = null) {
        val bundle = Bundle()

        if (email != null) {
            bundle.putString(EMAIL_BUNDLE_KEY, email)
        }

        val fragment = AuthEmailFragment().apply {
            arguments = bundle
        }

        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_right_exit_anim, R.animator.fragment_slide_left_exit_anim)
                .replace(R.id.fragment_auth_container, fragment)
                .commit()
    }

    fun showAuthCodeFragment(email: String, status: Int) {
        val bundle = Bundle()

        // Put email to the bundle
        bundle.putString(EMAIL_BUNDLE_KEY, email)
        bundle.putInt(AUTH_STATUS_BUNDLE_KEY, status)

        // Change fragment
        supportFragmentManager.apply {
            beginTransaction()
                    .setCustomAnimations(R.animator.fragment_slide_left_anim, R.animator.fragment_slide_right_anim)
                    .replace(R.id.fragment_auth_container, AuthConfirmFragment().apply {
                        arguments = bundle
                    }).commit()
        }
    }

    fun showAuthRegisterFragment(email: String) {
        val bundle = Bundle()

        bundle.putString(EMAIL_BUNDLE_KEY, email)

        supportFragmentManager.apply {
            beginTransaction()
                    .setCustomAnimations(R.animator.fragment_slide_left_anim, R.animator.fragment_slide_right_anim)
                    .replace(R.id.fragment_auth_container, AuthRegisterFragment().apply {
                        arguments = bundle
                    })
                    .commit()
        }
    }

    private fun showStartFragment(animate: Boolean) {
        if (animate) {
            supportFragmentManager.apply {
                beginTransaction()
                        .setCustomAnimations(R.animator.fragment_slide_right_exit_anim,
                                R.animator.fragment_slide_left_exit_anim)
                        .replace(R.id.fragment_auth_container, AuthEmailFragment())
                        .commit()
            }
        } else {
            supportFragmentManager.apply {
                beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.fragment_auth_container, AuthEmailFragment())
                        .commit()
            }
        }
    }

    fun showMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun showMessage(message: String) {
        showSnackbar(this, fragment_auth_container, message, Snackbar.LENGTH_LONG)
    }

    override fun onBackPressed() {
        authViewModel.disconnect()

        // Super!
        super.onBackPressed()
    }
}