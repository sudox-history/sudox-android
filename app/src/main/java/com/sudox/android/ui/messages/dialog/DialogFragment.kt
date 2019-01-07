package com.sudox.android.ui.messages.dialog

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.common.helpers.formatMessageText
import com.sudox.android.data.database.model.user.User
import com.sudox.android.ui.messages.MessagesInnerActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_messages_dialog.*
import javax.inject.Inject

class DialogFragment @Inject constructor() : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dialogAdapter: DialogAdapter

    private lateinit var messagesInnerActivity: MessagesInnerActivity
    private lateinit var recipientUser: User
    private lateinit var dialogViewModel: DialogViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        messagesInnerActivity = activity as MessagesInnerActivity
        recipientUser = (arguments!!.getSerializable(MessagesInnerActivity.RECIPIENT_USER_EXTRA) as User?)!!
        dialogViewModel = getViewModel(viewModelFactory)

        return inflater.inflate(R.layout.fragment_messages_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureToolbar()
        configureMessagesList()

        // Data-logic
        listenData()
    }

    private fun configureMessagesList() {
        val linearLayoutManager = LinearLayoutManager(context!!).apply { stackFromEnd = true }

        // Configure
        chatMessagesList.layoutManager = linearLayoutManager
        chatMessagesList.adapter = dialogAdapter

        // Paging ...
        chatMessagesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val position = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                val updatePosition = linearLayoutManager.itemCount - 1

                if (position == 0 && linearLayoutManager.itemCount >= 20) {
                    dialogViewModel.loadPartOfMessages(recipientUser.uid, updatePosition + 1)
                }
            }
        })
    }

    private fun listenData() {
        // Bind paging messages listener
        dialogViewModel.pagingDialogHistoryLiveData.observe(this, Observer {
            dialogAdapter.messages.addAll(0, it!!)
            dialogAdapter.notifyItemRangeInserted(0, it.size)
        })

        // Bind initial messages listener
        dialogViewModel.initialDialogHistoryLiveData.observe(this, Observer {
            dialogAdapter.messages = ArrayList(it!!)
            dialogAdapter.notifyDataSetChanged()

            // Start listen to new messages
            dialogViewModel.newDialogMessageLiveData.observe(this, Observer {
                dialogAdapter.messages.add(it!!)
                dialogAdapter.notifyItemInserted(dialogAdapter.messages.size - 1)
            })

            // Listen messages sending requests
            listenMessagesSendingRequests()
        })

        // Bind messages sending status listener
        dialogViewModel.sentMessageLiveData.observe(this, Observer { message ->
            val position = dialogAdapter.messages.indexOfFirst { it.lid == message!!.lid }

            // Message already saved :)
            if (position >= 0) {
                dialogAdapter.messages[position] = message!!
                dialogAdapter.notifyItemChanged(position)
            } else {
                dialogAdapter.messages.add(message!!)
                dialogAdapter.notifyItemInserted(dialogAdapter.messages.size - 1)
                chatMessagesList.scrollToPosition(dialogAdapter.messages.size - 1)
            }
        })

        // Bind recipient updates
        dialogViewModel.recipientUpdatesLiveData.observe(this, Observer {
            recipientUser = it!!

            // Update
            bindRecipient()
        })

        // Start business logic work
        dialogViewModel.start(recipientUser.uid)
    }

    private fun listenMessagesSendingRequests() = chatSendMessageButton.setOnClickListener {
        val text = formatMessageText(chatMessageTextField.text.toString())

        // Filter empty text
        if (text.isNotEmpty()) {
            dialogViewModel.sendTextMessage(recipientUser.uid, text)

            // Clear sent text
            chatMessageTextField.text = null
        }
    }

    private fun configureToolbar() {
        chatToolbar.setNavigationOnClickListener { activity!!.onBackPressed() }
        chatToolbar.inflateMenu(R.menu.menu_messages_chat_user)

        // Bind data
        bindRecipient()
    }

    private fun bindRecipient() {
        chatPeerAvatar.bindUser(recipientUser)
        chatPeerName.installText(recipientUser.name)
        chatPeerStatus.installText(recipientUser.nickname)
    }
}