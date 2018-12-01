package com.sudox.android.data.repositories.messages.chats

import com.sudox.android.common.helpers.formatMessageText
import com.sudox.android.data.database.dao.messages.ChatMessagesDao
import com.sudox.android.data.database.model.messages.ChatMessage
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.models.messages.MessageDirection
import com.sudox.android.data.models.common.LoadingType
import com.sudox.android.data.models.messages.MessageStatus
import com.sudox.android.data.models.messages.chats.dto.ChatHistoryDTO
import com.sudox.android.data.models.messages.chats.dto.ChatMessageDTO
import com.sudox.android.data.models.messages.chats.dto.NewChatMessageNotifyDTO
import com.sudox.android.data.models.messages.chats.dto.SendChatMessageDTO
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
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
    private val loadedRecipientChatsIds = hashMapOf<String, Int>()
    private val messagesSendingThreadContext = newSingleThreadContext("Sudox Messages Sending Queue")

    // PublishSubject для доставки новых сообщений.
    val globalNewMessagesChannel: BroadcastChannel<ChatMessage> = ConflatedBroadcastChannel()
    var globalSentMessageChannel: BroadcastChannel<ChatMessage> = ConflatedBroadcastChannel()
    var chatDialogNewMessageChannel: BroadcastChannel<ChatMessage>? = null
    var chatDialogHistoryChannel: BroadcastChannel<Pair<LoadingType, List<ChatMessage>>>? = null
    var chatDialogSentMessageChannel: BroadcastChannel<ChatMessage>? = null
    var openedChatRecipientId: String? = null

    // Защита от десинхронизации данных при скролле (от двух одновременных запросов)
    private var chatHistoryEnded: Boolean = false

    init {
        listenNewMessages()
        listenConnectionStatus()
    }

    private fun listenConnectionStatus() = GlobalScope.launch(Dispatchers.IO) {
        authRepository
                .accountSessionStateChannel
                .openSubscription()
                .filter { it }
                .consumeEach {
                    chatHistoryEnded = false
                    loadedRecipientChatsIds.clear() // Clean initial cache.

                    // Reload initial copy
                    if (openedChatRecipientId != null)
                        loadInitialMessages(openedChatRecipientId!!)

                    // Send delivering messages ...
                    sendDeliveringMessages()
                }
    }

    private fun sendDeliveringMessages() {
        chatMessagesDao
                .loadDeliveringMessages()
                .forEach { sendTextMessage(it.peer) }
    }

    private fun listenNewMessages() {
        protocolClient.listenMessage<NewChatMessageNotifyDTO>("updates.newMessage") {
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

    fun openChatDialog(recipientId: String) {
        openedChatRecipientId = recipientId
        chatDialogHistoryChannel = ConflatedBroadcastChannel()
        chatDialogSentMessageChannel = ConflatedBroadcastChannel()
        chatDialogNewMessageChannel = ConflatedBroadcastChannel()
    }

    fun endChatDialog() {
        openedChatRecipientId = null
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

    fun loadInitialMessages(recipientId: String) {
        if (!loadedRecipientChatsIds.contains(recipientId) && protocolClient.isValid()) {
            loadMessagesFromNetwork(recipientId)
        } else {
            loadMessagesFromDatabase(recipientId)
        }
    }

    fun loadPagedMessages(recipientId: String, offset: Int) {
        if (offset <= 0 || chatHistoryEnded) return

        // Get offset cache
        val cachedOffset = loadedRecipientChatsIds[recipientId]

        // Initial copy loaded from database
        if (!protocolClient.isValid() || cachedOffset != null && cachedOffset >= offset) {
            loadMessagesFromDatabase(recipientId, offset)
        } else {
            loadMessagesFromNetwork(recipientId, offset)
        }
    }

    private fun loadMessagesFromDatabase(recipientId: String, offset: Int = 0) = GlobalScope.launch {
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

    private fun loadMessagesFromNetwork(recipientId: String, offset: Int = 0) {
        // Execute request!
        protocolClient.makeRequest<ChatHistoryDTO>("chats.getHistory", ChatHistoryDTO().apply {
            this.id = recipientId
            this.limit = 20
            this.offset = offset
        }) {
            if (it.containsError()) {
                if (offset == 0) {
                    removeSavedMessages(recipientId)

                    // offset == 0 => first initializing
                    chatDialogHistoryChannel?.sendBlocking(Pair(LoadingType.INITIAL, chatMessagesDao.loadDeliveringMessages()))
                } else if (it.error == Errors.INVALID_USER) {
                    chatHistoryEnded = true
                }
            } else {
                // Message for storing
                val messages = toStorableMessages(it.messages)

                // Save messages into database & update offset cache (for offline mode supporting)
                chatMessagesDao.insertAll(messages)
                updateCachedOffset(recipientId, offset)

                // offset == 0 => first initializing
                if (offset == 0) {
                    chatMessagesDao.loadDeliveringMessages().apply { messages.plusAssign(this) }
                    chatDialogHistoryChannel?.sendBlocking(Pair(LoadingType.INITIAL, messages))
                } else {
                    chatDialogHistoryChannel?.sendBlocking(Pair(LoadingType.PAGING, messages))
                }
            }
        }
    }

    // TODO: Переместить отправку сообщения в отдельный канал!
    @Suppress("NAME_SHADOWING")
    fun sendTextMessage(recipientId: String, text: String) = GlobalScope.launch(messagesSendingThreadContext) {
        val accountId = accountRepository.cachedAccount?.id ?: return@launch
        val text = formatMessageText(text)

        // Запрет отправки пустого поля.
        if (text.isEmpty()) return@launch

        val message = ChatMessage(
                sender = accountId,
                peer = recipientId,
                message = text,
                date = System.currentTimeMillis(),
                direction = MessageDirection.TO,
                status = MessageStatus.IN_DELIVERY)

        // Save to database
        message.lid = chatMessagesDao.insertOne(message).toInt()

        // Notify subscribers, that message status was changed
        notifyMessageStatus(message, recipientId)

        // Sending ...
        val sendChatMessageDTO = protocolClient.makeRequest<SendChatMessageDTO>("chats.sendMessage", SendChatMessageDTO().apply {
            this.peerId = recipientId
            this.message = text
        }).await()

        if (sendChatMessageDTO.isSuccess()) {
            message.mid = sendChatMessageDTO.id
            message.date = sendChatMessageDTO.date
            message.status = MessageStatus.DELIVERED
        } else {
            message.status = MessageStatus.NOT_DELIVERED
        }

        // Update data ...
        chatMessagesDao.updateOne(message)

        // Notify subscribers, that message status was changed
        notifyMessageStatus(message, recipientId)
    }

    private suspend fun notifyMessageStatus(message: ChatMessage, recipientId: String) {
        // Notify subscribers, that message in delivery
        globalSentMessageChannel.send(message)

        // For current dialog
        if (openedChatRecipientId == recipientId)
            chatDialogSentMessageChannel?.send(message)
    }

    private fun updateCachedOffset(recipientId: String, offset: Int) {
        loadedRecipientChatsIds[recipientId] = offset // Allow load from this offset
    }

    private fun removeSavedMessages(recipientId: String, removeFromDb: Boolean = true) {
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