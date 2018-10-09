package com.sudox.android.ui.splash

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.sudox.android.R
import com.sudox.android.data.auth.AUTH_KEY
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.splash.enums.SplashAction
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class SplashActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get view model ...
        splashViewModel = getViewModel(viewModelFactory)
    }

    override fun onStart() {
        super.onStart()

        // Auth key для отличия запуска через AccountManager от обычного запуска
        val authKey = intent.getIntExtra(AUTH_KEY, 1)

        // Handle incoming actions ...
        splashViewModel.splashActionLiveData.observe(this, Observer {
            when (it) {
                SplashAction.SHOW_AUTH_ACTIVITY -> showAuthActivity(authKey)
                SplashAction.SHOW_MAIN_ACTIVITY -> showMainActivity()
                SplashAction.SHOW_ACCOUNT_EXISTS_ALERT -> showAccountExistsAlert()
            }
        })

        // Init authSession
        splashViewModel.initSession(this, authKey)
    }

    private fun showAccountExistsAlert() {
        AlertDialog.Builder(this)
                .setTitle(R.string.account_is_already_exist)
                .setMessage(R.string.account_is_already_created)
                .setOnCancelListener { finish() }
                .setPositiveButton(R.string.ok) { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.cancel()
                }.create().show()
    }

    private fun showAuthActivity(authKey: Int) {
        startActivity(Intent(this, AuthActivity::class.java)
                .apply {
                    putExtra(AUTH_KEY, authKey)
                })

        finish()
    }

    private fun showMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}