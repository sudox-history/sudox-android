package com.sudox.android.ui

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.sudox.android.R
import com.sudox.android.common.Data
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.enums.TokenState
import com.sudox.android.common.helpers.showSnackbar
import com.sudox.android.common.models.TokenData
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.main.ContactsFragment
import com.sudox.android.ui.splash.SplashActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = getViewModel(viewModelFactory)

        mainViewModel.accountLiveData.observe(this, Observer { account ->
            mainViewModel.connectLiveData.observe(this, Observer{
                getConnectState(account, it)
            })
        })


        // init listeners
        mainViewModel.initContactsListeners()
        mainViewModel.loadContacts()

        supportFragmentManager.
            beginTransaction()
                    .replace(R.id.fragment_main_container, ContactsFragment())
                    .commit()
    }

    private fun getConnectState(account: SudoxAccount?, connectData: Data<ConnectState>) {
        when (connectData.data) {
            ConnectState.RECONNECTED -> {
                showMessage(getString(R.string.lost_internet_connection))
                mainViewModel.sendToken(account).observe(this, Observer(::getTokenState))
            }
            ConnectState.DISCONNECTED -> showMessage(getString(R.string.lost_internet_connection))
        }
    }

    private fun getTokenState(tokenData: TokenData) {
        if(tokenData.tokenState == TokenState.WRONG){
            mainViewModel.removeAllContacts()
            mainViewModel.disconnect()
            showSplashActivity()
        }
    }

    private fun showSplashActivity() {
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }

    fun showMessage(message: String) {
        showSnackbar(this, fragment_main_container, message, Snackbar.LENGTH_LONG)
    }

    override fun onBackPressed() {
        mainViewModel.disconnect()
        super.onBackPressed()
    }
}