package com.sudox.android.data.repositories.messages.chats

import com.sudox.android.data.database.dao.messages.ChatMessagesDao
import com.sudox.android.data.database.model.messages.ChatMessage
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.models.common.LoadingType
import com.sudox.android.data.models.messages.MessageDirection
import com.sudox.android.data.models.messages.chats.Dialog
import com.sudox.android.data.models.messages.chats.dto.ChatsLastMessagesDTO
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.main.UsersRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.NetworkException
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DialogsRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                            private val chatMessagesRepository: ChatMessagesRepository,
                                            private val usersRepository: UsersRepository,
                                            private val authRepository: AuthRepository,
                                            private val chatMessagesDao: ChatMessagesDao) {

    // Initial & paging copies of chats
    var dialogsChannel: BroadcastChannel<Pair<LoadingType, List<Dialog>>> = ConflatedBroadcastChannel()
    var dialogsUpdatesChannel: BroadcastChannel<Dialog> = ConflatedBroadcastChannel()

    // Dialogs loaded from server (if value negative - last messages not loaded)
    private var lastMessagesLoadedOffset: Int = -1
    private var lastMessagesEnded: Boolean = false
    private var isWorking: Boolean = false

    /**
     * Запускает слушателей событий ...
     */
    fun startWork() {
        if (!isWorking) {
            listenConnectionStatus()
            listenNewMessages()
            listenSendingMessages()
        }

        // Защита от двух и более запусков слушателей
        isWorking = true
    }

    private fun listenNewMessages() = GlobalScope.launch(Dispatchers.IO) {
        for (dialog in chatMessagesRepository
                .globalNewMessagesChannel
                .openSubscription()) {

            // Notify that dialog was updated
            updateDialog(dialog)
        }
    }

    private fun listenSendingMessages() = GlobalScope.launch(Dispatchers.IO) {
        for (dialog in chatMessagesRepository
                .globalSentMessageChannel
                .openSubscription()) {

            // Notify that dialog was updated
            updateDialog(dialog)
        }
    }

    private fun updateDialog(message: ChatMessage) {
        val dialog = getDialog(message) ?: return

        // Notify ...
        dialogsUpdatesChannel.offer(dialog)
    }

    private fun listenConnectionStatus() = GlobalScope.launch {
        authRepository
                .accountSessionStateChannel
                .openSubscription()
                .filter { it }
                .consumeEach {
                    resetLoadedOffset()
                    loadInitialDialogs()
                }
    }

    fun loadInitialDialogs() = GlobalScope.launch {
        if (lastMessagesLoadedOffset < 0 && authRepository.sessionIsValid && protocolClient.isValid()) {
            runBlocking {
                loadDialogsFromDatabase()
                loadDialogsFromNetwork()
            }
        } else {
            loadDialogsFromDatabase()
        }
    }

    fun loadPagedDialogs(offset: Int) {
        if (offset <= 0) return

        // Initial copy loaded from database
        if (!protocolClient.isValid() || lastMessagesLoadedOffset >= offset) {
            loadDialogsFromDatabase(offset)
        } else if (!lastMessagesEnded) {
            loadDialogsFromNetwork(offset)
        }
    }

    private fun loadDialogsFromNetwork(offset: Int = 0) = GlobalScope.launch(Dispatchers.IO) {
        try {
            val chatsLastMessagesDTO = protocolClient.makeRequestWithControl<ChatsLastMessagesDTO>("chats.getChats", ChatsLastMessagesDTO().apply {
                this.limit = 10
                this.offset = offset
            }).await()

            if (chatsLastMessagesDTO.isSuccess()) {
                val messages = chatMessagesRepository.toStorableMessages(chatsLastMessagesDTO.messages)
                val dialogs = getDialogs(messages)

                // Cache ...
                chatMessagesDao.insertAll(messages)
                lastMessagesLoadedOffset = offset

                // offset == 0 => first initializing
                if (offset == 0) {
                    dialogsChannel.offer(Pair(LoadingType.INITIAL, dialogs))
                } else if (dialogs.isNotEmpty()) {
                    dialogsChannel.offer(Pair(LoadingType.PAGING, dialogs))
                }
            } else {
                if (offset == 0) {
                    resetLoadedOffset()
                    chatMessagesRepository.removeAllSavedMessages()
                    dialogsChannel.offer(Pair(LoadingType.INITIAL, arrayListOf()))
                } else if (chatsLastMessagesDTO.error == Errors.EMPTY_CHATS) {
                    lastMessagesEnded = true
                }
            }
        } catch (e: NetworkException) {
            // TODO: Обрыв соединения.
        }
    }

    private fun loadDialogsFromDatabase(offset: Int = 0) = GlobalScope.launch(Dispatchers.IO) {
        val messages = chatMessagesDao.loadAll(offset, 10)

        // Сообщения могут отсутствовать в БД
        if (messages.isEmpty()) {
            if (offset == 0) {
                resetLoadedOffset()

                // offset == 0 => first initializing
                dialogsChannel.offer(Pair(LoadingType.INITIAL, arrayListOf()))
            }
        } else {
            val dialogs = getDialogs(messages)

            // offset == 0 => first initializing
            if (offset == 0) {
                dialogsChannel.offer(Pair(LoadingType.INITIAL, dialogs))
            } else if (dialogs.isNotEmpty()) {
                dialogsChannel.offer(Pair(LoadingType.PAGING, dialogs))
            }
        }
    }

    /**
     * Ищет собеседника к сообщению.
     * Если собеседник не будет найден, то сообщение не будет отражено в результатах вызова данной функции.
     */
    private suspend fun getDialogs(messages: List<ChatMessage>): ArrayList<Dialog> {
        val userIdsForLoading = messages.map { if (it.direction == MessageDirection.TO) it.peer else it.sender }
        val users = usersRepository.loadUsers(userIdsForLoading).await()
        val dialogs = arrayListOf<Dialog>()

        // Mapping
        messages.forEach {
            val recipientId = if (it.direction == MessageDirection.TO) it.peer else it.sender
            val recipient = users.find { it.uid == recipientId }

            // Recipient was loaded
            if (recipient != null) dialogs.plusAssign(Dialog(recipient, it))
        }

        return dialogs
    }

    /**
     * Ищет собеседника к сообщению
     * Если собеседник не будет найден, то сообщение не будет отражено в результатах вызова данной функции.
     */
    private fun getDialog(message: ChatMessage): Dialog? = runBlocking {
        val userId = if (message.direction == MessageDirection.TO) message.peer else message.sender
        val user = usersRepository.loadUser(userId).await() ?: return@runBlocking null

        // Create dialog
        return@runBlocking Dialog(user, message)
    }

    private fun resetLoadedOffset() {
        lastMessagesEnded = false
        lastMessagesLoadedOffset = -1
    }
}