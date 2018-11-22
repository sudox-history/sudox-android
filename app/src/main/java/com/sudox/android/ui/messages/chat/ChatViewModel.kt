package com.sudox.android.ui.messages.chat

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.database.model.ChatMessage
import com.sudox.android.data.models.chats.ChatLoadingType
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.messages.ChatMessagesRepository
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatViewModel @Inject constructor(val chatMessagesRepository: ChatMessagesRepository,
                                        val authRepository: AuthRepository) : ViewModel() {

    val initialChatHistoryLiveData: MutableLiveData<List<ChatMessage>> = SingleLiveEvent()
    val pagingChatHistoryLiveData: MutableLiveData<List<ChatMessage>> = SingleLiveEvent()

    // Subscriptions
    private var newMessagesSubscription: ReceiveChannel<ChatMessage>? = null
    private var messagesHistorySubscription: ReceiveChannel<Pair<ChatLoadingType, List<ChatMessage>>>? = null

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
        GlobalScope.async {
            newMessagesSubscription!!.consumeEach {

            }
        }

        GlobalScope.async {
            messagesHistorySubscription!!.consumeEach {
                val loadingType = it.first
                val messages = it.second

                if (loadingType == ChatLoadingType.INITIAL) {
                    initialChatHistoryLiveData.postValue(messages)
                } else if (loadingType == ChatLoadingType.PAGING) {
                    pagingChatHistoryLiveData.postValue(messages)
                }
            }
        }

        chatMessagesRepository.loadInitialMessages(recipientId)
    }

    fun loadPartOfMessages(recipientId: String, offset: Int) = GlobalScope.launch {
        chatMessagesRepository.loadPagedMessages(recipientId, offset)
    }

    override fun onCleared() {
        // Kill subscriptions
        newMessagesSubscription?.cancel()
        messagesHistorySubscription?.cancel()

        // Close dialog
        chatMessagesRepository.endChatDialog()
    }
}