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
import kotlinx.coroutines.*
import javax.inject.Inject

class DialogViewModel @Inject constructor(val dialogsMessagesRepository: DialogsMessagesRepository,
                                          val authRepository: AuthRepository) : ViewModel() {

    val sentMessageLiveData: MutableLiveData<DialogMessage> = SingleLiveEvent()
    val newDialogMessagesLiveData: MutableLiveData<List<DialogMessage>> = SingleLiveEvent()
    val initialDialogHistoryLiveData: MutableLiveData<ArrayList<DialogMessage>> = SingleLiveEvent()
    val pagingDialogHistoryLiveData: MutableLiveData<ArrayList<DialogMessage>> = SingleLiveEvent()
    val recipientUpdatesLiveData: MutableLiveData<User> = SingleLiveEvent()

    private val subscriptionsContainer: SubscriptionsContainer = SubscriptionsContainer()
    private var isLoading: Boolean = false
    private var isListEnded: Boolean = false
    private var lastLoadedOffset: Int = 0
    private var loadedMessagesCount: Int = 0
    private var recipientId: Long = 0
    private var firstDeliveredMessageId: Long = -1
    private var updateJob: Job? = null

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

            // Only delivered messages
            if (message.mid > 0L) {
                firstDeliveredMessageId = message.mid
                loadedMessagesCount++
                lastLoadedOffset++
            }

            newDialogMessagesLiveData.postValue(listOf(message))
        }
    }

    private fun listenSentMessages() = GlobalScope.launch {
        for (message in subscriptionsContainer
                .addSubscription(dialogsMessagesRepository
                        .dialogDialogSentMessageChannel!!
                        .openSubscription())) {

            // Only delivered messages
            if (message.mid > 0L) {
                firstDeliveredMessageId = message.mid
                loadedMessagesCount++
                lastLoadedOffset++
            }

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
                    updateJob = updateMessages()
                }
            }
        }
    }

    private fun updateMessages() = GlobalScope.async(Dispatchers.IO) {
        isLoading = true

        // Начинаем поиск новых сообщений с 0
        var currentOffset = 0
        val partSize = Math.min(100, loadedMessagesCount)
        val newMessages = arrayListOf<DialogMessage>()

        outer@ while (isActive) {
            val part = dialogsMessagesRepository
                    .loadMessages(recipientId, currentOffset, partSize, onlyFromNetwork = true, excludeDelivering = true)
                    .await()

            if (part.isEmpty()) break

            // Увеличиваем offset для следующего прохода
            currentOffset += part.size

            // Ищем сходство, если за цикл не найдем
            for (i in part.lastIndex downTo 0) {
                val message = part[i]

                if (message.mid > firstDeliveredMessageId) {
                    newMessages.add(0, message)
                } else {
                    break@outer
                }
            }
        }

        // Recalculate data
        lastLoadedOffset += newMessages.size
        loadedMessagesCount += newMessages.size
        firstDeliveredMessageId = newMessages.find { it.mid != 0L }?.mid ?: firstDeliveredMessageId

        // To showing
        newDialogMessagesLiveData.postValue(newMessages)
        isLoading = false
    }

    fun loadMessages(offset: Int = 0, limit: Int = 20) {
        if (isLoading || isListEnded || offset in 1..lastLoadedOffset) return

        // Загрузим сообщения ...
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Блокируем дальнейшие действия
                isLoading = true

                // Запрашиваем список сообщений ...
                val messages = dialogsMessagesRepository
                        .loadMessages(recipientId, offset, limit)
                        .await()

                if (offset == 0) {
                    initialDialogHistoryLiveData.postValue(messages)
                    loadedMessagesCount = messages.size

                    if (loadedMessagesCount > 0) {
                        firstDeliveredMessageId = messages.findLast { it.mid != 0L }?.mid ?: -1
                    } else {
                        firstDeliveredMessageId = -1
                    }
                } else {
                    pagingDialogHistoryLiveData.postValue(messages)
                    loadedMessagesCount += messages.size
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
        updateJob?.cancel()
        subscriptionsContainer.unsubscribeAll()
        dialogsMessagesRepository.endDialog()
    }
}