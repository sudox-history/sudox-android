package com.sudox.android.ui.splash

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
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

        // Handle incoming actions ...
        splashViewModel.splashActionLiveData.observe(this, Observer {
            when (it) {
                SplashAction.SHOW_AUTH_ACTIVITY -> showAuthActivity()
                SplashAction.SHOW_MAIN_ACTIVITY -> showMainActivity()
            }
        })

        // Init authSession
        splashViewModel.initSession(this)
    }

    private fun showAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    private fun showMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}