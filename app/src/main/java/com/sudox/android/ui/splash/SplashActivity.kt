package com.sudox.android.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sudox.android.R
import com.sudox.android.common.Data
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.enums.TokenState
import com.sudox.android.common.models.TokenData
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.auth.AuthActivity
import dagger.android.support.DaggerAppCompatActivity
import timber.log.Timber
import javax.inject.Inject

class SplashActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Special easy way by Antonio with my corrections for project!
        splashViewModel = getViewModel(viewModelFactory)
        splashViewModel.connectLiveData.observe(this, Observer(::getConnectState))
        splashViewModel.connect()
    }

    private fun getConnectState(connectData: Data<ConnectState>) {
        when (connectData.data) {
            ConnectState.CONNECTED -> {
                splashViewModel.sendToken().observe(this, Observer(::getTokenState))
                Timber.log(1, "start handshake")
            }

            ConnectState.CONNECT_ERROR -> {
                chooseActivity(splashViewModel.getAccount()?.token)
                Timber.log(0, "choosing activity")
            }
        }
    }

    private fun getTokenState(data: TokenData) {
        when (data.tokenState) {
            TokenState.CORRECT -> {
                showMainActivity()
                Timber.log(0, "show main activity")
            }
            TokenState.WRONG -> {
                showAuthActivity()
                Timber.log(0, "show auth activity")
            }
            TokenState.MISSING -> {
                showAuthActivity()
                Timber.log(0, "show auth activity")
            }
        }
    }

    private fun chooseActivity(token: String?) {
        when (token) {
            null -> {
                showAuthActivity()
                Timber.log(0, "show auth activity")
            }
            else -> {
                showMainActivity()
                Timber.log(0, "show main activity")
            }
        }
    }

    private fun showMainActivity() {

    }

    private fun showAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)

        // Start activity
        startActivity(intent)

        // Close old activity
        finish()
    }

    override fun onBackPressed() {
        splashViewModel.disconnect()

        // Back pressed
        super.onBackPressed()
    }
}