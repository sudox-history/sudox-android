package com.sudox.android.ui.main.contacts.add

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import dagger.android.support.DaggerAppCompatDialogFragment
import kotlinx.android.synthetic.main.dialog_invite_friend.*
import javax.inject.Inject

class InviteFriendDialogFragment @Inject constructor() : DaggerAppCompatDialogFragment() {

    private lateinit var contactAddFragment: ContactAddFragment

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contactAddFragment = parentFragment as ContactAddFragment

        return inflater.inflate(R.layout.dialog_invite_friend, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDialogListener()
    }

    private fun initDialogListener() {
        cancelAction.setOnClickListener {
            dismiss()
        }

        inviteAction.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        val smsIntent = Intent(Intent.ACTION_VIEW)
        smsIntent.type = "vnd.android-dir/mms-sms"
        smsIntent.putExtra("address", "+7${contactAddFragment.phoneNumber}")
        smsIntent.putExtra("sms_body", getString(R.string.invite_friend_sms))
        startActivity(smsIntent)
    }
}