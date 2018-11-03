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
import com.sudox.android.data.models.Errors
import com.sudox.android.data.models.avatar.AvatarInfo
import com.sudox.android.data.models.avatar.impl.ColorAvatarInfo
import com.sudox.android.data.models.chats.UserChatRecipient
import com.sudox.android.data.repositories.messages.CHAT_MESSAGES_SIZE
import com.sudox.android.ui.adapters.ChatAdapter
import com.sudox.android.ui.messages.MessagesActivity
import com.sudox.design.helpers.drawAvatar
import com.sudox.design.helpers.drawCircleBitmap
import com.sudox.design.helpers.getTwoFirstLetters
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_messages_chat_user.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.async
import javax.inject.Inject

class ChatFragment @Inject constructor() : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var messagesActivity: MessagesActivity
    private lateinit var userChatRecipient: UserChatRecipient
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        messagesActivity = activity as MessagesActivity
        userChatRecipient = arguments!!.getParcelable(MessagesActivity.CONVERSATION_RECIPIENT_KEY)!!
        chatViewModel = getViewModel(viewModelFactory)

        return inflater.inflate(R.layout.fragment_messages_chat_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureToolbar()
        configureMessagesList()
        configureButtons()
    }

    private fun configureToolbar() {
        // Inflating the menu ...
        userChatToolbar.inflateMenu(R.menu.menu_messages_chat_user)
        userChatToolbar.setNavigationOnClickListener { activity!!.onBackPressed() }
        userChatToolbar.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener true
        }

        // Get avatar type
        val avatarInfo = AvatarInfo.parse(userChatRecipient.photo)

        if (avatarInfo is ColorAvatarInfo) {
            drawCircleBitmap(context!!,
                    drawAvatar(text = userChatRecipient.name.getTwoFirstLetters(),
                            firstColor = avatarInfo.firstColor,
                            secondColor = avatarInfo.secondColor),
                    chatRecipientAvatar)
        } else {
            // TODO: Implement
        }

        // Bind data
        chatRecipientName.text = userChatRecipient.name
        chatRecipientLastJoin.text = userChatRecipient.nickname
    }

    private fun configureMessagesList() {
        chatAdapter = ChatAdapter(ArrayList(), messagesActivity)
        chatViewModel.authRepository.accountSessionLiveData.observe(this, Observer {
            if (it!!.lived) loadInitialMessages()
        })

        // Layout manager
        val linearLayoutManager = LinearLayoutManager(messagesActivity)

        // Set parameters
        chatMessagesList.itemAnimator = null
        chatMessagesList.layoutManager = linearLayoutManager.apply { stackFromEnd = true }
        chatMessagesList.adapter = chatAdapter
        chatMessagesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val position = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                val updatePosition = linearLayoutManager.itemCount - 1

                if (position == 0 && linearLayoutManager.itemCount >= CHAT_MESSAGES_SIZE) {
                    chatViewModel.chatRepository.getHistory(userChatRecipient.uid, updatePosition + 1, {
                        GlobalScope.async(Dispatchers.Main) {
                            chatAdapter.items.addAll(0, it.reversed())
                            chatAdapter.notifyItemRangeInserted(0, it.size)
                        }
                    }, {
                        if (it == Errors.INVALID_PARAMETERS || it == Errors.INVALID_USER) {
                            activity!!.onBackPressed()
                        }
                    })
                }
            }
        })

        loadInitialMessages()

        chatViewModel.chatRepository.newMessageLiveData.observe(this, Observer {
            if (it!!.sender == userChatRecipient.uid || it.peer == userChatRecipient.uid) {
                chatAdapter.items.add(it)
                chatAdapter.notifyItemInserted(chatAdapter.items.size - 1)
                chatMessagesList.scrollToPosition(chatAdapter.items.size - 1)
            }
        })
    }

    private fun loadInitialMessages() {
        chatViewModel.chatRepository.getInitialHistory(userChatRecipient.uid, {
            GlobalScope.async(Dispatchers.Main) {
                chatAdapter.items = ArrayList(it.reversed())
                chatAdapter.notifyItemRangeInserted(0, it.size)
            }
        }, {
            if (it == Errors.INVALID_PARAMETERS || it == Errors.INVALID_USER) {
                activity!!.onBackPressed()
            }
        })
    }


    private fun configureButtons() {
        send_message_button.setOnClickListener {
            val message = formatMessage(edit_message_field.text.toString())

            if (message.isNotEmpty()) {
                chatViewModel.chatRepository.sendSimpleMessage(userChatRecipient.uid, message)
                edit_message_field.text = null
            }
        }
    }
}