package com.sudox.android.ui.activity.splashscreen

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.sudox.android.R
import com.sudox.android.common.Data
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.enums.HandshakeState
import com.sudox.android.common.enums.TokenState
import com.sudox.android.viewmodel.observe
import com.sudox.android.viewmodel.withViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Special easy way by Antonio Leiva with my corrections for project!
        withViewModel<SplashViewModel>(viewModelFactory){
            splashViewModel = this
            connect()
            observe(connectData, ::getConnectState)
            observe(handshakeData, ::getHandshakeState)
            observe(tokenData, ::getTokenState)
        }
    }

    private fun getToken() : String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return sharedPreferences.getString("TOKEN", null)
    }

    private fun getConnectState(data: Data<ConnectState>){
        data.let {
            when(it.data){
                ConnectState.SUCCESS -> splashViewModel.startHandshake()
                ConnectState.ERROR -> TODO("go to auth or main activity (depends on token)")
            }
        }
    }

    private fun getHandshakeState(data: Data<HandshakeState>){
        data.let {
            when (it.data) {
                HandshakeState.SUCCESS -> splashViewModel.sendToken(getToken())
                HandshakeState.ERROR -> splashViewModel.connect() //retry connect
            }
        }
    }

    private fun getTokenState(data: Data<TokenState>){
        data.let {
            when (it.data) {
                TokenState.CORRECT -> TODO("go to main activity")
                TokenState.WRONG -> TODO("go to auth activity")
                TokenState.MISSING -> TODO("go to auth activity")
            }
        }
    }

}