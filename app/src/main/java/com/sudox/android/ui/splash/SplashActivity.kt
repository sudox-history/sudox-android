package com.sudox.android.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.sudox.android.R
import com.sudox.android.common.Data
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.enums.HandshakeState
import com.sudox.android.common.enums.TokenState
import com.sudox.android.common.models.TokenData
import com.sudox.android.common.viewmodels.observe
import com.sudox.android.common.viewmodels.withViewModel
import com.sudox.android.ui.auth.AuthActivity
import dagger.android.support.DaggerAppCompatActivity
import timber.log.Timber
import javax.inject.Inject

class SplashActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Special easy way by Antonio with my corrections for project!
        splashViewModel = withViewModel(viewModelFactory) {
            connect()

            // Observe data
            observe(connectData, ::getConnectState)
            observe(handshakeData, ::getHandshakeState)
            observe(tokenData, ::getTokenState)
        }
    }

    private fun getConnectState(connectData: Data<ConnectState>) {
        when (connectData.data) {
            ConnectState.SUCCESS -> {
                splashViewModel.startHandshake()
                Timber.log(1, "start handshake")
            }
            ConnectState.ERROR -> {
                chooseActivity(splashViewModel.getAccount()?.token)
                Timber.log(0, "choosing activity")
            }
        }
    }

    private fun getHandshakeState(handshakeData: Data<HandshakeState>) {
        when (handshakeData.data) {
            HandshakeState.SUCCESS -> {
                splashViewModel.sendToken()
                Timber.log(1, "send token action")
            }
            HandshakeState.ERROR -> {
                splashViewModel.connect()
                Timber.log(2, "connect action")
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

    private fun chooseActivity(token: String?){
        when(token){
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        super.onBackPressed()
    }
}