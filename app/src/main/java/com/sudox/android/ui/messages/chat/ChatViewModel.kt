package com.sudox.android.ui.messages.chat

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.SubscriptionsContainer
import com.sudox.android.data.database.model.messages.ChatMessage
import com.sudox.android.data.models.common.LoadingType
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.messages.chats.ChatMessagesRepository
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatViewModel @Inject constructor(val chatMessagesRepository: ChatMessagesRepository,
                                        val authRepository: AuthRepository) : ViewModel() {

    val sentMessageLiveData: MutableLiveData<ChatMessage> = SingleLiveEvent()
    val newChatMessageLiveData: MutableLiveData<ChatMessage> = SingleLiveEvent()
    val initialChatHistoryLiveData: MutableLiveData<List<ChatMessage>> = SingleLiveEvent()
    val pagingChatHistoryLiveData: MutableLiveData<List<ChatMessage>> = SingleLiveEvent()

    // Subscriptions
    private val subscriptionsContainer: SubscriptionsContainer = SubscriptionsContainer()

    fun start(recipientId: Long) = GlobalScope.launch {
        chatMessagesRepository.openChatDialog(recipientId)

        // Set callback for new messages receiving
        GlobalScope.launch {
            subscriptionsContainer.addSubscription(chatMessagesRepository
                    .chatDialogNewMessageChannel!!
                    .openSubscription())
                    .consumeEach { newChatMessageLiveData.postValue(it) }
        }

        // Set callback for chat history loading
        GlobalScope.launch {
            subscriptionsContainer.addSubscription(chatMessagesRepository
                    .chatDialogHistoryChannel!!
                    .openSubscription())
                    .consumeEach {
                        val loadingType = it.first
                        val messages = it.second

                        if (loadingType == LoadingType.INITIAL) {
                            initialChatHistoryLiveData.postValue(messages)
                        } else if (loadingType == LoadingType.PAGING) {
                            pagingChatHistoryLiveData.postValue(messages)
                        }
                    }
        }

        // Set callback for messages sending
        GlobalScope.launch {
            subscriptionsContainer.addSubscription(chatMessagesRepository
                    .chatDialogSentMessageChannel!!
                    .openSubscription())
                    .consumeEach { sentMessageLiveData.postValue(it) }
        }

        chatMessagesRepository.loadInitialMessages(recipientId)
    }

    fun loadPartOfMessages(recipientId: Long, offset: Int) = GlobalScope.launch(Dispatchers.IO) {
        chatMessagesRepository.loadPagedMessages(recipientId, offset)
    }

    fun sendTextMessage(recipientId: Long, text: String) {
        chatMessagesRepository.sendTextMessage(recipientId, text)
    }

    override fun onCleared() {
        subscriptionsContainer.unsubscribeAll()
        chatMessagesRepository.endChatDialog()
    }
}