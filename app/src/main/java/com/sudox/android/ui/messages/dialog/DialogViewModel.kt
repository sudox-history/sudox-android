package com.sudox.android.ui.messages.dialog

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.SubscriptionsContainer
import com.sudox.android.data.database.model.messages.DialogMessage
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.exceptions.InternalRequestException
import com.sudox.android.data.models.common.InternalErrors
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.messages.dialogs.DialogsMessagesRepository
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class DialogViewModel @Inject constructor(val dialogsMessagesRepository: DialogsMessagesRepository,
                                          val authRepository: AuthRepository) : ViewModel() {

    val sentMessageLiveData: MutableLiveData<DialogMessage> = SingleLiveEvent()
    val newDialogMessageLiveData: MutableLiveData<DialogMessage> = SingleLiveEvent()
    val initialDialogHistoryLiveData: MutableLiveData<ArrayList<DialogMessage>> = SingleLiveEvent()
    val pagingDialogHistoryLiveData: MutableLiveData<ArrayList<DialogMessage>> = SingleLiveEvent()
    val recipientUpdatesLiveData: MutableLiveData<User> = SingleLiveEvent()

    private val subscriptionsContainer: SubscriptionsContainer = SubscriptionsContainer()
    private var isLoading: Boolean = false
    private var isListEnded: Boolean = false
    private var lastLoadedOffset: Int = 0
    private var loadedMessagesCount: Int = 0
    private var recipientId: Long = 0

    fun start(recipientId: Long) = GlobalScope.launch {
        dialogsMessagesRepository.openDialog(recipientId)

        // Listen updates
        listenNewMessages()
        listenSentMessages()
        listenRecipientUpdates()
        listenAccountSession()

        // Save recipient
        this@DialogViewModel.recipientId = recipientId

        // Start loading ...
        loadMessages()
    }

    private fun listenNewMessages() = GlobalScope.launch {
        for (message in subscriptionsContainer
                .addSubscription(dialogsMessagesRepository
                        .dialogDialogNewMessageChannel!!
                        .openSubscription())) {

            newDialogMessageLiveData.postValue(message)
        }
    }

    private fun listenSentMessages() = GlobalScope.launch {
        for (message in subscriptionsContainer
                .addSubscription(dialogsMessagesRepository
                        .dialogDialogSentMessageChannel!!
                        .openSubscription())) {

            sentMessageLiveData.postValue(message)
        }
    }

    private fun listenRecipientUpdates() = GlobalScope.launch {
        for (user in subscriptionsContainer
                .addSubscription(dialogsMessagesRepository
                        .dialogRecipientUpdateChannel!!
                        .openSubscription())) {

            recipientUpdatesLiveData.postValue(user)
        }
    }

    private fun listenAccountSession() = GlobalScope.launch {
        for (state in subscriptionsContainer
                .addSubscription(authRepository
                        .accountSessionStateChannel
                        .openSubscription())) {

            // Если не успеем подгрузить с сети во время загрузки фрагмента.
            if (state) {
                if (loadedMessagesCount == 0) {
                    loadMessages()
                } else {
//                    updateDialogs()
                }
            }
        }
    }

    fun loadMessages(offset: Int = 0, limit: Int = 20) {
        if (isLoading || isListEnded || offset in 1..lastLoadedOffset) return

        // Загрузим сообщения ...
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Блокируем дальнейшие действия
                isLoading = true

                // Запрашиваем список сообщений ...
                val dialogs = dialogsMessagesRepository
                        .loadMessages(recipientId, offset, limit)
                        .await()

                if (offset == 0) {
                    initialDialogHistoryLiveData.postValue(dialogs)
                    loadedMessagesCount = dialogs.size
                } else {
                    pagingDialogHistoryLiveData.postValue(dialogs)
                    loadedMessagesCount += dialogs.size
                }

                lastLoadedOffset = offset
            } catch (e: InternalRequestException) {
                if (e.errorCode == InternalErrors.LIST_ENDED) {
                    isListEnded = true
                }
            }

            // Загрузка завершена, разблокируем дальнейшие действия.
            isLoading = false
        }
    }

    fun sendTextMessage(recipientId: Long, text: String) {
        dialogsMessagesRepository.sendTextMessage(recipientId, text)
    }

    override fun onCleared() {
        subscriptionsContainer.unsubscribeAll()
        dialogsMessagesRepository.endDialog()
    }
}