package com.sudox.android.ui.messages

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import com.sudox.android.R
import com.sudox.android.ui.messages.dialog.DialogFragment
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MessagesInnerActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    companion object {
        const val RECIPIENT_USER_EXTRA = "user_recipient"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_inner)

        // TODO: Get talks, channels ...
        val user = intent.getSerializableExtra(RECIPIENT_USER_EXTRA)

        // TODO: Check talks, channels != null
        if (user != null) {
            showDialogFragment()
        } else {

        }
    }

    private fun showDialogFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentChatContainer, DialogFragment().apply { arguments = intent.extras })
                .commit()
    }
}