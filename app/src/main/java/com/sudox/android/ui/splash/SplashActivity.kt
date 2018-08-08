package com.sudox.android.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sudox.android.R
import com.sudox.android.common.Data
import com.sudox.android.common.auth.AUTH_CODE
import com.sudox.android.common.auth.AUTH_KEY
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.enums.TokenState
import com.sudox.android.common.models.TokenData
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.MainActivity
import com.sudox.android.ui.auth.AuthActivity
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class SplashActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // For add account (MAY BE ONLY ONE ACCOUNT)
        if (intent.getIntExtra(AUTH_KEY, 1) == AUTH_CODE) {
            showAuthActivity()
        }

        splashViewModel = getViewModel(viewModelFactory)
    }

    override fun onStart() {
        super.onStart()

        splashViewModel.connectLiveData.observe(this, Observer(::getConnectState))
        splashViewModel.connect()
    }

    private fun getConnectState(connectData: Data<ConnectState>) {
        when (connectData.data) {
            ConnectState.CONNECTED -> splashViewModel.sendToken()
                    .observe(this, Observer(::getTokenState))
            ConnectState.CONNECT_ERROR -> {
                chooseActivity(splashViewModel.getAccount())
            }
        }
    }

    private fun getTokenState(data: TokenData) {
        when (data.tokenState) {
            TokenState.CORRECT -> showMainActivity()
            TokenState.WRONG -> showAuthActivity()
            TokenState.MISSING -> showAuthActivity()
        }
    }

    private fun chooseActivity(account: SudoxAccount?) {
        when (account) {
            null -> {
                Handler().postDelayed({
                    showAuthActivity()
                }, 500)
            }
            else -> showMainActivity()
        }
    }

    private fun showMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        splashViewModel.disconnect()

        // Super!
        super.onBackPressed()
    }
}