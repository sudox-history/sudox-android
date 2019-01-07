package com.sudox.android.ui.messages.dialog

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.SubscriptionsContainer
import com.sudox.android.data.database.model.messages.DialogMessage
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.models.common.LoadingType
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.messages.dialogs.DialogsMessagesRepository
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class DialogViewModel @Inject constructor(val dialogsMessagesRepository: DialogsMessagesRepository,
                                          val authRepository: AuthRepository) : ViewModel() {

    val sentMessageLiveData: MutableLiveData<DialogMessage> = SingleLiveEvent()
    val newDialogMessageLiveData: MutableLiveData<DialogMessage> = SingleLiveEvent()
    val initialDialogHistoryLiveData: MutableLiveData<List<DialogMessage>> = SingleLiveEvent()
    val pagingDialogHistoryLiveData: MutableLiveData<List<DialogMessage>> = SingleLiveEvent()
    val recipientUpdatesLiveData: MutableLiveData<User> = SingleLiveEvent()

    // Subscriptions
    private val subscriptionsContainer: SubscriptionsContainer = SubscriptionsContainer()

    fun start(recipientId: Long) = GlobalScope.launch {
        dialogsMessagesRepository.openDialog(recipientId)

        // Set callback for new messages receiving
        GlobalScope.launch {
            subscriptionsContainer.addSubscription(dialogsMessagesRepository
                    .dialogDialogNewMessageChannel!!
                    .openSubscription())
                    .consumeEach { newDialogMessageLiveData.postValue(it) }
        }

        // Set callback for chat history loading
        GlobalScope.launch {
            subscriptionsContainer.addSubscription(dialogsMessagesRepository
                    .dialogDialogHistoryChannel!!
                    .openSubscription())
                    .consumeEach {
                        val loadingType = it.first
                        val messages = it.second

                        if (loadingType == LoadingType.INITIAL) {
                            initialDialogHistoryLiveData.postValue(messages)
                        } else if (loadingType == LoadingType.PAGING) {
                            pagingDialogHistoryLiveData.postValue(messages)
                        }
                    }
        }

        // Set callback for messages sending
        GlobalScope.launch {
            subscriptionsContainer.addSubscription(dialogsMessagesRepository
                    .dialogDialogSentMessageChannel!!
                    .openSubscription())
                    .consumeEach { sentMessageLiveData.postValue(it) }
        }

        // Set callback for recipient updates
        GlobalScope.launch {
            subscriptionsContainer.addSubscription(dialogsMessagesRepository
                    .dialogRecipientUpdateChannel!!
                    .openSubscription())
                    .consumeEach { recipientUpdatesLiveData.postValue(it) }
        }

        dialogsMessagesRepository.loadInitialMessages(recipientId)
    }

    fun loadPartOfMessages(recipientId: Long, offset: Int) = GlobalScope.launch(Dispatchers.IO) {
        dialogsMessagesRepository.loadPagedMessages(recipientId, offset)
    }

    fun sendTextMessage(recipientId: Long, text: String) {
        dialogsMessagesRepository.sendTextMessage(recipientId, text)
    }

    override fun onCleared() {
        subscriptionsContainer.unsubscribeAll()
        dialogsMessagesRepository.endDialog()
    }
}