package com.sudox.android.ui.messages

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import com.sudox.android.R
import com.sudox.android.data.models.messages.chats.enums.ChatType
import com.sudox.android.ui.messages.chat.ChatFragment
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MessagesInnerActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_inner)

        // TODO: Get talks, channels ...
        var user = intent.getSerializableExtra(RECIPIENT_USER_EXTRA)

        // TODO: Check talks, channels != null
        if (user != null) {
            showChatFragment()
        } else {

        }
    }

    private fun showChatFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentChatContainer, ChatFragment().apply { arguments = intent.extras })
                .commit()
    }

    companion object {
        const val RECIPIENT_USER_EXTRA = "user_recipient"
    }
}