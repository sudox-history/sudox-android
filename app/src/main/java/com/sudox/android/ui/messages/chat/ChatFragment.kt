package com.sudox.android.ui.messages.chat

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.data.models.avatar.AvatarInfo
import com.sudox.android.data.models.avatar.impl.ColorAvatarInfo
import com.sudox.android.data.models.chats.UserChatRecipient
import com.sudox.android.ui.adapters.ChatAdapter
import com.sudox.android.ui.diffutil.UserChatMessagesDiffUtil
import com.sudox.android.ui.messages.MessagesActivity
import com.sudox.design.helpers.drawAvatar
import com.sudox.design.helpers.drawCircleBitmap
import com.sudox.design.helpers.getTwoFirstLetters
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_messages_chat_user.*
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
        chatMessagesList.layoutManager = LinearLayoutManager(messagesActivity)
                .apply { stackFromEnd = true }
        chatMessagesList.adapter = chatAdapter

        // Подписываемся на обновление данных
        chatViewModel
                .chatRepository
                .observeWithCurrentRecipient(userChatRecipient.uid)
                .observe(this, Observer {
                    val oldSize = chatAdapter.items.size
                    val newSize = it!!.size
                    val sizesDifference = newSize - oldSize
                    val diffUtil = UserChatMessagesDiffUtil(it, chatAdapter.items)
                    val diffResult = DiffUtil.calculateDiff(diffUtil)

                    // Update data ...
                    chatAdapter.items = it

                    // Notify chatAdapter about update

                    chatAdapter.notifyItemInserted(sizesDifference)
                    chatMessagesList.scrollToPosition(newSize - 1)
                })
    }


    private fun configureButtons() {
        send_message_button.setOnClickListener {
            chatViewModel.chatRepository.sendSimpleMessage(userChatRecipient.uid,
                    edit_message_field.text.toString())
        }
    }
}