package com.sudox.android.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import com.androidadvance.topsnackbar.TSnackbar
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.common.helpers.showTopSnackbar
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.main.contacts.ContactsFragment
import com.sudox.android.ui.main.enums.MainActivityAction
import com.sudox.protocol.models.enums.ConnectState
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mainViewModel: MainViewModel

    // Fragments
    @Inject
    lateinit var contactsFragment: ContactsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get view model ...
        mainViewModel = getViewModel(viewModelFactory)

        // Listen actions ...
        mainViewModel.mainActivityActionsLiveData.observe(this, Observer {
            if (it == MainActivityAction.SHOW_AUTH_FRAGMENT) {
                showAuthActivity()
            }
        })

        // Слушаем изменения состояния соединения ...
        mainViewModel.connectionStateLiveData.observe(this, Observer {
            if (it == ConnectState.DISCONNECTED) showMessage(getString(R.string.lost_internet_connection))
        })

        // Listen session state ...
        mainViewModel.listenSessionChanges(this)

        // Настраиваем BottomNavigationView
        initBottomNavigationView()
    }

    fun initBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            val id = it.itemId

            if (id == R.id.contacts_item) {
                showContactsFragment()
            } else {
                return@setOnNavigationItemSelectedListener false
            }

            return@setOnNavigationItemSelectedListener true
        }

        // Set default fragment
        bottomNavigationView.selectedItemId = R.id.contacts_item
    }

    private fun showContactsFragment() {
        supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragmentMainContainer, contactsFragment)
                .addToBackStack(null)
                .commit()
    }

    fun showMessage(message: String) {
        showTopSnackbar(this, fragmentMainContainer, message, TSnackbar.LENGTH_LONG)
    }

    private fun showAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}