package com.sudox.android.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.sudox.android.R
import com.sudox.android.common.Data
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.models.dto.AuthHashDTO
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.MainActivity
import com.sudox.android.ui.auth.confirm.AUTH_STATUS
import com.sudox.android.ui.auth.confirm.AuthConfirmFragment
import com.sudox.android.ui.auth.confirm.EMAIL_BUNDLE_KEY
import com.sudox.android.ui.auth.email.AuthEmailFragment
import com.sudox.android.ui.auth.register.AuthRegisterFragment
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

internal val CURRENT_FRAGMENT_KEY = "current_fragment"

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

        showStartFragment()
    }

    private fun getConnectState(connectData: Data<ConnectState>) {
        when (connectData.data) {
            ConnectState.RECONNECTED -> {
                showMessage(getString(R.string.connection_restored))
                if (hash != null) importAuthHash(hash!!)

            }
            ConnectState.DISCONNECTED -> showMessage(getString(R.string.lost_internet_connection))
        }
    }

    private fun importAuthHash(hash: String) {
        authViewModel.importAuthHash(hash)
                .observe(this, Observer(::getHashData))
    }

    private fun getHashData(authHashDTO: AuthHashDTO) {
        if (authHashDTO.code == 0) {
            showStartFragment()
        }
    }

    fun showAuthEmailFragment(email: String) {
        val bundle = Bundle()

        bundle.putString(EMAIL_BUNDLE_KEY, email)

        supportFragmentManager.apply {
            beginTransaction()
                    .setCustomAnimations(R.animator.fragment_slide_right_exit_anim,
                            R.animator.fragment_slide_left_exit_anim)
                    .replace(R.id.fragment_auth_container, AuthEmailFragment().apply {
                        arguments = bundle
                    })
                    .commit()
        }
    }

    fun showAuthCodeFragment(email: String, status: Int) {
        val bundle = Bundle()

        // Put email to the bundle
        bundle.putString(EMAIL_BUNDLE_KEY, email)
        bundle.putInt(AUTH_STATUS, status)

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

    private fun showStartFragment() {
        supportFragmentManager.apply {
            beginTransaction()
                    .setCustomAnimations(R.animator.fragment_slide_right_exit_anim,
                            R.animator.fragment_slide_left_exit_anim)
                    .replace(R.id.fragment_auth_container, AuthEmailFragment())
                    .commit()
        }
    }

    fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun showMessage(message: String) {
        Snackbar.make(fragment_auth_container, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        authViewModel.disconnect()
        super.onBackPressed()
    }
}