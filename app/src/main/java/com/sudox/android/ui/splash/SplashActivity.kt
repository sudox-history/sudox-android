package com.sudox.android.ui.splash

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sudox.android.R
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
        splashViewModel = getViewModel(viewModelFactory)

        // Get account from database
        splashViewModel.getAccount().observe(this, Observer { sudoxAccount ->
            // Add account from AccountManager context
            if (intent.getIntExtra(AUTH_KEY, 1) == AUTH_CODE && sudoxAccount != null) {
                AlertDialog.Builder(this)
                        .setTitle(R.string.account_is_already_exist)
                        .setMessage(R.string.account_is_already_created)
                        .setOnCancelListener { finish() }
                        .setPositiveButton(R.string.ok) { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.cancel()
                        }.create().show()
            } else {
                splashViewModel.connectLiveData.observe(this, Observer {
                    getConnectState(sudoxAccount, it)
                })

                splashViewModel.connect()
            }
        })
    }

    private fun getConnectState(sudoxAccount: SudoxAccount?, connectState: ConnectState) {
        if (connectState == ConnectState.CONNECTED) {
            splashViewModel
                    .sendToken(sudoxAccount)
                    .observe(this, Observer(::getTokenState))

            if (sudoxAccount == null) {
                splashViewModel.removeAllData()
            }
        } else if (connectState == ConnectState.CONNECT_ERROR) {
            chooseActivity(sudoxAccount, connectState)
        }
    }

    private fun getTokenState(data: TokenData) {
        when (data.tokenState) {
            TokenState.CORRECT -> showMainActivity()
            TokenState.WRONG -> showAuthActivity()
            TokenState.MISSING -> showAuthActivity()
        }
    }

    private fun chooseActivity(account: SudoxAccount?, connectState: ConnectState) {
        if (account == null) {
            if (connectState == ConnectState.CONNECT_ERROR) {
                Handler().postDelayed(::showAuthActivity, 500)
            } else {
                showAuthActivity()
            }
        } else {
            showMainActivity()
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