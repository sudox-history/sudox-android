package com.sudox.android.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.sudox.android.R
import com.sudox.android.common.Data
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.enums.HandshakeState
import com.sudox.android.common.enums.TokenState
import com.sudox.android.common.viewmodels.observe
import com.sudox.android.common.viewmodels.withViewModel
import com.sudox.android.ui.auth.AuthActivity
import dagger.android.support.DaggerAppCompatActivity
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

    private fun getConnectState(data: Data<ConnectState>) {
        data.let {
            when (it.data) {
                ConnectState.SUCCESS -> splashViewModel.startHandshake()
                ConnectState.ERROR -> TODO("go to auth or main activity (depends on token)")
            }
        }
    }

    private fun getHandshakeState(data: Data<HandshakeState>) {
        data.let {
            when (it.data) {
                HandshakeState.SUCCESS -> splashViewModel.sendToken()
                HandshakeState.ERROR -> splashViewModel.connect()
            }
        }
    }

    private fun getTokenState(data: Data<TokenState>) {
        data.let {
            when (it.data) {
                TokenState.CORRECT -> TODO("go to main activity")
                TokenState.WRONG -> showAuthActivity()
                TokenState.MISSING -> showAuthActivity()
            }
        }
    }

    private fun showAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)

        // Start activity
        startActivity(intent)

        // Close old activity
        finish()
    }
}