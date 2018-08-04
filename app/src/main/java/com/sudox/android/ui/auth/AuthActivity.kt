package com.sudox.android.ui.auth

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.sudox.android.R
import com.sudox.android.common.Data
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.auth.email.AuthEmailFragment
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        authViewModel = getViewModel(viewModelFactory)
        authViewModel.connectLiveData.observe(this, Observer(::getConnectState))

        // Start fragment
        supportFragmentManager.apply {
            beginTransaction()
                    .add(R.id.fragment_auth_container, AuthEmailFragment())
                    .commit()
        }
    }

    private fun getConnectState(connectData: Data<ConnectState>) {
        when (connectData.data) {
            ConnectState.RECONNECTED -> showMessage(getString(R.string.connection_restored))
            ConnectState.DISCONNECTED -> showMessage(getString(R.string.lost_internet_connection))
        }
    }

    fun showMessage(message: String) {
        Snackbar.make(fragment_auth_container, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        authViewModel.disconnect()
        super.onBackPressed()
    }
}