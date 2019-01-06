package com.sudox.android.data.repositories.messages.chats

import com.sudox.android.common.helpers.formatMessageText
import com.sudox.android.data.database.dao.messages.ChatMessagesDao
import com.sudox.android.data.database.model.messages.ChatMessage
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.models.common.LoadingType
import com.sudox.android.data.models.messages.MessageDirection
import com.sudox.android.data.models.messages.MessageStatus
import com.sudox.android.data.models.messages.chats.dto.ChatHistoryDTO
import com.sudox.android.data.models.messages.chats.dto.ChatMessageDTO
import com.sudox.android.data.models.messages.chats.dto.SendChatMessageDTO
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.NetworkException
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatMessagesRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                                 private val authRepository: AuthRepository,
                                                 private val accountRepository: AccountRepository,
                                                 private val chatMessagesDao: ChatMessagesDao) {

    // ID загруженных чатов ...
    private val loadedRecipientChatsIds = hashMapOf<Long, Int>()
    private val messagesSendingThreadContext = newSingleThreadContext("Sudox Messages Sending Queue")

    // PublishSubject для доставки новых сообщений.
    val globalNewMessagesChannel: BroadcastChannel<ChatMessage> = ConflatedBroadcastChannel()
    var globalSentMessageChannel: BroadcastChannel<ChatMessage> = ConflatedBroadcastChannel()
    var chatDialogNewMessageChannel: BroadcastChannel<ChatMessage>? = null
    var chatDialogHistoryChannel: BroadcastChannel<Pair<LoadingType, List<ChatMessage>>>? = null
    var chatDialogSentMessageChannel: BroadcastChannel<ChatMessage>? = null
    var openedChatRecipientId: Long = 0

    // Защита от десинхронизации данных при скролле (от двух одновременных запросов)
    private var chatHistoryEnded: Boolean = false

    init {
        listenNewMessages()
        listenSessionStatus()
    }

    private fun listenSessionStatus() = GlobalScope.launch {
        authRepository
                .accountSessionStateChannel
                .openSubscription()
                .filter { it }
                .consumeEach {
                    chatHistoryEnded = false
                    loadedRecipientChatsIds.clear() // Clean initial cache.

                    // Reload initial copy
                    if (openedChatRecipientId != 0L)
                        loadInitialMessages(openedChatRecipientId)
                }
    }

    private fun listenNewMessages() {
        protocolClient.listenMessage<ChatMessageDTO>("updates.newMessage") {
            val accountId = accountRepository.cachedAccount?.id ?: return@listenMessage
            val recipientId = if (it.peer == accountId) it.sender else it.peer
            val direction = if (it.peer != accountId) MessageDirection.TO else MessageDirection.FROM
            val message = ChatMessage(
                    mid = it.id,
                    sender = it.sender,
                    peer = it.peer,
                    message = it.message,
                    date = it.date,
                    direction = direction,
                    status = MessageStatus.DELIVERED)

            // Insert into database
            chatMessagesDao.insertOne(message)

            // Notify listeners about new message
            globalNewMessagesChannel.sendBlocking(message)

            // If current dialog active notify subscribers
            if (openedChatRecipientId == recipientId) {
                chatDialogNewMessageChannel?.sendBlocking(message)
            }
        }
    }

    fun openChatDialog(recipientId: Long) {
        openedChatRecipientId = recipientId
        chatDialogHistoryChannel = ConflatedBroadcastChannel()
        chatDialogSentMessageChannel = ConflatedBroadcastChannel()
        chatDialogNewMessageChannel = ConflatedBroadcastChannel()
    }

    fun endChatDialog() {
        openedChatRecipientId = 0
        chatHistoryEnded = false

        // Close old channel
        if (chatDialogNewMessageChannel != null && !chatDialogNewMessageChannel!!.isClosedForSend) {
            chatDialogNewMessageChannel!!.close()
        }

        if (chatDialogHistoryChannel != null && !chatDialogHistoryChannel!!.isClosedForSend) {
            chatDialogHistoryChannel!!.close()
        }

        if (chatDialogSentMessageChannel != null && !chatDialogSentMessageChannel!!.isClosedForSend) {
            chatDialogSentMessageChannel!!.close()
        }
    }

    fun loadInitialMessages(recipientId: Long) = GlobalScope.launch {
        if (!loadedRecipientChatsIds.contains(recipientId) && protocolClient.isValid()) {
            loadMessagesFromNetwork(recipientId)
        } else {
            loadMessagesFromDatabase(recipientId)
        }
    }

    fun loadPagedMessages(recipientId: Long, offset: Int) = GlobalScope.launch {
        if (offset <= 0 || chatHistoryEnded) return@launch

        // Get offset cache
        val cachedOffset = loadedRecipientChatsIds[recipientId]

        // Initial copy loaded from database
        if (!protocolClient.isValid() || cachedOffset != null && cachedOffset >= offset) {
            loadMessagesFromDatabase(recipientId, offset)
        } else {
            loadMessagesFromNetwork(recipientId, offset)
        }
    }

    private fun loadMessagesFromDatabase(recipientId: Long, offset: Int = 0) = GlobalScope.launch(Dispatchers.IO) {
        val messages = chatMessagesDao.loadAll(recipientId, offset, 20).sortedBy { it.date }

        // Remove from cache
        if (messages.isEmpty()) {
            if (offset == 0) {
                removeSavedMessages(recipientId, false)
                chatDialogHistoryChannel?.sendBlocking(Pair(LoadingType.INITIAL, messages))
            }
        } else {
            if (offset == 0) {
                chatDialogHistoryChannel?.sendBlocking(Pair(LoadingType.INITIAL, messages))
            } else {
                chatDialogHistoryChannel?.sendBlocking(Pair(LoadingType.PAGING, messages))
            }
        }
    }

    private fun loadMessagesFromNetwork(recipientId: Long, offset: Int = 0) = GlobalScope.launch(Dispatchers.IO) {
        try {
            val chatHistoryDTO = protocolClient.makeRequestWithControl<ChatHistoryDTO>("dialogs.getHistory", ChatHistoryDTO().apply {
                this.id = recipientId
                this.limit = 20
                this.offset = offset
            }).await()

            if (chatHistoryDTO.isSuccess()) {
                val messages = toStorableMessages(chatHistoryDTO.messages)

                // Save messages into database & update offset cache (for offline mode supporting)
                chatMessagesDao.insertAll(messages)
                updateCachedOffset(recipientId, offset)

                // offset == 0 => first initializing
                if (offset == 0) {
                    chatMessagesDao.loadDeliveringMessages(recipientId).apply { messages.plusAssign(this) }
                    chatDialogHistoryChannel?.sendBlocking(Pair(LoadingType.INITIAL, messages))
                } else {
                    chatDialogHistoryChannel?.sendBlocking(Pair(LoadingType.PAGING, messages))
                }
            } else {
                if (offset == 0) {
                    removeSavedMessages(recipientId)

                    // offset == 0 => first initializing
                    chatDialogHistoryChannel?.sendBlocking(Pair(LoadingType.INITIAL, chatMessagesDao.loadDeliveringMessages(recipientId)))
                } else if (chatHistoryDTO.error == Errors.INVALID_USER) {
                    chatHistoryEnded = true
                }
            }
        } catch (e: NetworkException) {
            // Nothing ...
        }
    }

    // TODO: Переместить отправку сообщения в отдельный канал!
    @Suppress("NAME_SHADOWING")
    fun sendTextMessage(recipientId: Long, text: String) = GlobalScope.launch(Dispatchers.IO) {
        val accountId = accountRepository.cachedAccount?.id ?: return@launch
        val text = formatMessageText(text)

        // Запрет отправки пустого поля.
        if (text.isEmpty()) return@launch

        // Отправка ...
        sendMessage(ChatMessage(
                sender = accountId,
                peer = recipientId,
                message = text,
                date = System.currentTimeMillis(),
                direction = MessageDirection.TO,
                status = MessageStatus.IN_DELIVERY)).await()
    }

    private suspend fun sendMessage(message: ChatMessage) = GlobalScope.async(messagesSendingThreadContext) {
        // It's new message
        if (message.lid == 0) {
            message.lid = chatMessagesDao.insertOne(message).toInt()
        }

        // Change status of message
        message.status = MessageStatus.IN_DELIVERY

        // Notify subscribers, that message status was changed
        notifyMessageStatus(message, message.peer)

        // Sending ...
        try {
            val sendChatMessageDTO = protocolClient.makeRequestWithControl<SendChatMessageDTO>("dialogs.send", SendChatMessageDTO().apply {
                this.peerId = message.peer
                this.message = message.message
            }).await()

            if (sendChatMessageDTO.isSuccess()) {
                message.mid = sendChatMessageDTO.id
                message.date = sendChatMessageDTO.date
                message.status = MessageStatus.DELIVERED
            } else {
                message.status = MessageStatus.NOT_DELIVERED
            }
        } catch (e: NetworkException) {
            message.status = MessageStatus.NOT_DELIVERED
        }

        // Update data ...
        chatMessagesDao.updateOne(message)

        // Notify subscribers, that message status was changed
        notifyMessageStatus(message, message.peer)
    }

    private suspend fun notifyMessageStatus(message: ChatMessage, recipientId: Long) {
        // Notify subscribers, that message in delivery
        globalSentMessageChannel.send(message)

        // For current dialog
        if (openedChatRecipientId == recipientId)
            chatDialogSentMessageChannel?.send(message)
    }

    private fun updateCachedOffset(recipientId: Long, offset: Int) {
        loadedRecipientChatsIds[recipientId] = offset // Allow load from this offset
    }

    private fun removeSavedMessages(recipientId: Long, removeFromDb: Boolean = true) {
        loadedRecipientChatsIds.minusAssign(recipientId)

        // Экономим на запросах к БД.
        if (removeFromDb) chatMessagesDao.removeAll(recipientId)
    }

    internal fun removeAllSavedMessages(removeFromDb: Boolean = true) {
        loadedRecipientChatsIds.clear()

        // Экономим на запросах к БД.
        if (removeFromDb) chatMessagesDao.removeAll()
    }

    internal fun toStorableMessages(messages: ArrayList<ChatMessageDTO>): ArrayList<ChatMessage> {
        val accountId = accountRepository.cachedAccount?.id ?: return arrayListOf()

        return ArrayList(messages.map {
            val direction = if (it.peer != accountId) MessageDirection.TO else MessageDirection.FROM

            ChatMessage(
                    mid = it.id,
                    sender = it.sender,
                    peer = it.peer,
                    message = it.message,
                    date = it.date,
                    direction = direction,
                    status = MessageStatus.DELIVERED)
        })
    }
}