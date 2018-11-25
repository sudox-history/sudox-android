package com.sudox.android.ui.messages.chat

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.database.model.messages.ChatMessage
import com.sudox.android.data.models.LoadingType
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.messages.chats.ChatMessagesRepository
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatViewModel @Inject constructor(val chatMessagesRepository: ChatMessagesRepository,
                                        val authRepository: AuthRepository) : ViewModel() {

    val newChatMessageLiveData: MutableLiveData<ChatMessage> = SingleLiveEvent()
    val initialChatHistoryLiveData: MutableLiveData<List<ChatMessage>> = SingleLiveEvent()
    val pagingChatHistoryLiveData: MutableLiveData<List<ChatMessage>> = SingleLiveEvent()

    // Subscriptions
    private var newMessagesSubscription: ReceiveChannel<ChatMessage>? = null
    private var messagesHistorySubscription: ReceiveChannel<Pair<LoadingType, List<ChatMessage>>>? = null

    fun start(recipientId: String) = GlobalScope.launch {
        chatMessagesRepository.openChatDialog(recipientId)

        // Subscribe to new messages
        newMessagesSubscription = chatMessagesRepository
                .chatDialogNewMessageChannel!!
                .openSubscription()

        // Subscribe to messages history
        messagesHistorySubscription = chatMessagesRepository
                .chatDialogHistoryChannel!!
                .openSubscription()

        // Set callback for new messages receiving
        GlobalScope.launch {
            newMessagesSubscription!!.consumeEach { newChatMessageLiveData.postValue(it) }
        }

        GlobalScope.launch {
            messagesHistorySubscription!!.consumeEach {
                val loadingType = it.first
                val messages = it.second

                if (loadingType == LoadingType.INITIAL) {
                    initialChatHistoryLiveData.postValue(messages)
                } else if (loadingType == LoadingType.PAGING) {
                    pagingChatHistoryLiveData.postValue(messages)
                }
            }
        }

        chatMessagesRepository.loadInitialMessages(recipientId)
    }

    fun loadPartOfMessages(recipientId: String, offset: Int) = GlobalScope.launch {
        chatMessagesRepository.loadPagedMessages(recipientId, offset)
    }

    fun sendTextMessage(recipientId: String, text: String) {
        chatMessagesRepository.sendTextMessage(recipientId, text)
    }

    override fun onCleared() {
        newMessagesSubscription?.cancel()
        messagesHistorySubscription?.cancel()

        // Close dialog
        chatMessagesRepository.endChatDialog()
    }
}