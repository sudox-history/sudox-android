package com.sudox.android.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.data.database.model.user.User
import com.sudox.android.ui.FragmentNavigator
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.main.contacts.ContactsFragment
import com.sudox.android.ui.main.enums.MainActivityAction
import com.sudox.android.ui.main.messages.MessagesFragment
import com.sudox.android.ui.main.profile.ProfileFragment
import com.sudox.android.ui.messages.MessagesInnerActivity
import com.sudox.protocol.ProtocolClient
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var protocolClient: ProtocolClient
    lateinit var mainViewModel: MainViewModel
    lateinit var fragmentNavigator: FragmentNavigator

    // Fragments
    @Inject
    lateinit var profileFragment: ProfileFragment
    @Inject
    lateinit var contactsFragment: ContactsFragment
    @Inject
    lateinit var messagesFragment: MessagesFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get view model ...
        mainViewModel = getViewModel(viewModelFactory)
        mainViewModel.mainActivityActionsLiveData.observe(this, Observer {
            if (it == MainActivityAction.SHOW_AUTH_ACTIVITY) {
                showAuthActivity()
            }
        })

        // All root fragments - reusable
        fragmentNavigator = FragmentNavigator(
                this,
                supportFragmentManager,
                arrayListOf(profileFragment, contactsFragment, messagesFragment),
                R.id.fragmentMainContainer)

        // Listen changes
        mainViewModel.listenSessionChanges()

        // Настраиваем BottomNavigationView
        initBottomNavigationView()
    }

    private fun initBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when {
                it.itemId == R.id.contacts_item -> fragmentNavigator.showRootFragment(contactsFragment)
                it.itemId == R.id.messages_item -> fragmentNavigator.showRootFragment(messagesFragment)
                it.itemId == R.id.profile_item -> fragmentNavigator.showRootFragment(profileFragment)
                else -> return@setOnNavigationItemSelectedListener false
            }

            return@setOnNavigationItemSelectedListener true
        }

        // Set default fragment
        bottomNavigationView.selectedItemId = R.id.messages_item
    }

    private fun showAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    fun showChatWithUser(user: User) {
        startActivity(Intent(this, MessagesInnerActivity::class.java).apply {
            putExtra(MessagesInnerActivity.RECIPIENT_USER_EXTRA, user)
        })
    }

    override fun onBackPressed() {
        // Выход из приложения, т.к. фрагментов больше нет
        if (!fragmentNavigator.popBackstack()) super.onBackPressed()
    }
}