package com.sudox.android.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.data.models.chats.ChatType
import com.sudox.android.data.models.chats.UserChatRecipient
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

        // Listen actions ...
        mainViewModel.mainActivityActionsLiveData.observe(this, Observer {
            if (it == MainActivityAction.SHOW_AUTH_ACTIVITY) {
                showAuthActivity()
            }
        })

        // Listen session state ...
        mainViewModel.listenSessionChanges(this)

        // Настраиваем BottomNavigationView
        initBottomNavigationView()
    }

    private fun initBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when {
                it.itemId == R.id.contacts_item -> showContactsFragment()
                it.itemId == R.id.messages_item -> showMessagesFragment()
                it.itemId == R.id.profile_item -> showProfileFragment()
                else -> return@setOnNavigationItemSelectedListener false
            }

            return@setOnNavigationItemSelectedListener true
        }

        // Set default fragment
        bottomNavigationView.selectedItemId = R.id.messages_item
    }

    private fun showMessagesFragment() {
        var fragment = supportFragmentManager.findFragmentByTag("messages")

        if (fragment == null) {
            fragment = MessagesFragment()
        }

        supportFragmentManager.beginTransaction()
//                .setCustomAnimations(R.animator.animator_fragment_change, 0)
                .replace(R.id.fragmentMainContainer, fragment, "messages")
                .commit()
    }

    private fun showContactsFragment() {
        var fragment = supportFragmentManager.findFragmentByTag("contacts")

        if (fragment == null) {
            fragment = ContactsFragment()
        }

        supportFragmentManager.beginTransaction()
//                .setCustomAnimations(R.animator.animator_fragment_change, 0)
                .replace(R.id.fragmentMainContainer, fragment, "contacts")
                .commit()
    }

    private fun showProfileFragment() {
        var fragment = supportFragmentManager.findFragmentByTag("profile")

        if (fragment == null) {
            fragment = profileFragment
        }

        supportFragmentManager.beginTransaction()
//                .setCustomAnimations(R.animator.animator_fragment_change, 0)
                .replace(R.id.fragmentMainContainer, fragment, "profile")
                .commit()
    }

    private fun showAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    fun showChatWithUser(userChatRecipient: UserChatRecipient) {
        startActivity(Intent(this, MessagesInnerActivity::class.java).apply {
            putExtra(MessagesInnerActivity.CONVERSATION_TYPE_KEY, ChatType.CHAT.ordinal)
            putExtra(MessagesInnerActivity.CONVERSATION_RECIPIENT_KEY, userChatRecipient)
        })
    }
}