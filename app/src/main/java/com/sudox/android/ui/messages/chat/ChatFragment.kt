package com.sudox.android.ui.messages.chat

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
import com.sudox.android.common.helpers.formatMessage
import com.sudox.android.data.database.model.User
import com.sudox.android.data.models.avatar.AvatarInfo
import com.sudox.android.data.models.avatar.impl.ColorAvatarInfo
import com.sudox.android.data.models.messages.chats.UserChatRecipient
import com.sudox.android.ui.main.common.BaseReconnectFragment
import com.sudox.android.ui.messages.MessagesInnerActivity
import com.sudox.design.helpers.drawAvatar
import com.sudox.design.helpers.drawCircleBitmap
import com.sudox.design.helpers.getTwoFirstLetters
import kotlinx.android.synthetic.main.fragment_messages_chat_user.*
import javax.inject.Inject

class ChatFragment @Inject constructor() : BaseReconnectFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var chatAdapter: ChatAdapter

    private lateinit var messagesInnerActivity: MessagesInnerActivity
    private lateinit var recipientUser: User
    private lateinit var chatViewModel: ChatViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        messagesInnerActivity = activity as MessagesInnerActivity
        recipientUser = (arguments!!.getSerializable(MessagesInnerActivity.RECIPIENT_USER_EXTRA) as User?)!!
        chatViewModel = getViewModel(viewModelFactory)

        return inflater.inflate(R.layout.fragment_messages_chat_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureToolbar()
        configureMessagesList()

        // Data-logic
        listenForConnection()
        listenData()
    }

    private fun configureMessagesList() {
        val linearLayoutManager = LinearLayoutManager(context!!).apply { stackFromEnd = true }

        // Configure
        chatMessagesList.layoutManager = linearLayoutManager
        chatMessagesList.adapter = chatAdapter

        // Paging ...
        chatMessagesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val position = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                val updatePosition = linearLayoutManager.itemCount - 1

                if (position == 0 && linearLayoutManager.itemCount >= 20) {
                    chatViewModel.loadPartOfMessages(recipientUser.uid, updatePosition + 1)
                }
            }
        })
    }

    private fun listenData() {
        // Bind paging messages listener
        chatViewModel.pagingChatHistoryLiveData.observe(this, Observer {
            chatAdapter.messages.addAll(0, it!!)
            chatAdapter.notifyItemRangeInserted(0, it.size)
        })

        // Bind initial messages listener
        chatViewModel.initialChatHistoryLiveData.observe(this, Observer {
            chatAdapter.messages = ArrayList(it!!)
            chatAdapter.notifyDataSetChanged()

            // Start listen to new messages
            chatViewModel.newChatMessageLiveData.observe(this, Observer {
                chatAdapter.messages.add(it!!)
                chatAdapter.notifyItemInserted(chatAdapter.messages.size - 1)

                // Scroll to bottom
                chatMessagesList.scrollToPosition(chatAdapter.messages.size - 1)
            })

            // Listen messages sending requests
            listenMessagesSendingRequests()
        })

        // Start business logic work
        chatViewModel.start(recipientUser.uid)
    }

    private fun listenMessagesSendingRequests() = chatSendMessageButton.setOnClickListener {
        val text = formatMessage(chatMessageTextField.text.toString())

        // Filter empty text
        if (text.isNotEmpty()) {
            chatViewModel.sendTextMessage(recipientUser.uid, text)

            // Clear sent text
            chatMessageTextField.text = null
        }
    }

    private fun configureToolbar() {
        // Inflating the menu ...
        userChatToolbar.inflateMenu(R.menu.menu_messages_chat_user)
        userChatToolbar.setNavigationOnClickListener { activity!!.onBackPressed() }
        userChatToolbar.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener true
        }

        // Get avatar type
        val avatarInfo = AvatarInfo.parse(recipientUser.photo)

        if (avatarInfo is ColorAvatarInfo) {
            drawCircleBitmap(context!!,
                    drawAvatar(text = recipientUser.name.getTwoFirstLetters(),
                            firstColor = avatarInfo.firstColor,
                            secondColor = avatarInfo.secondColor),
                    chatRecipientAvatar)
        } else {
            // TODO: Implement
        }

        // Bind data
        chatRecipientName.text = recipientUser.name
        chatRecipientLastJoin.text = recipientUser.nickname
    }

    override fun showConnectionStatus(isConnect: Boolean) {
        if (isConnect) {
            chatRecipientLastJoin.text = recipientUser.nickname
        } else {
            chatRecipientLastJoin.text = getString(R.string.wait_for_connect)
        }
    }
}