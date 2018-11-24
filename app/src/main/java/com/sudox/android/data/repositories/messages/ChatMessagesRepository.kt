package com.sudox.android.data.repositories.messages

import com.sudox.android.common.helpers.formatMessage
import com.sudox.android.data.database.dao.messages.ChatMessagesDao
import com.sudox.android.data.database.model.messages.ChatMessage
import com.sudox.android.data.models.Errors
import com.sudox.android.data.models.messages.MessageDirection
import com.sudox.android.data.models.messages.chats.ChatLoadingType
import com.sudox.android.data.models.messages.chats.dto.ChatHistoryDTO
import com.sudox.android.data.models.messages.chats.dto.NewChatMessageNotifyDTO
import com.sudox.android.data.models.messages.chats.dto.SendChatMessageDTO
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatMessagesRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                                 private val authRepository: AuthRepository,
                                                 private val accountRepository: AccountRepository,
                                                 private val chatMessagesDao: ChatMessagesDao) {

    // ID загруженных чатов ...
    private val loadedRecipientChatsIds = hashMapOf<String, Int>()

    // PublishSubject для доставки новых сообщений.
    val globalNewMessagesChannel: BroadcastChannel<ChatMessage> = ConflatedBroadcastChannel()
    var chatDialogNewMessageChannel: BroadcastChannel<ChatMessage>? = null
    var chatDialogHistoryChannel: BroadcastChannel<Pair<ChatLoadingType, List<ChatMessage>>>? = null
    var openedChatRecipientId: String? = null

    // Защита от десинхронизации данных при скролле (от двух одновременных запросов)
    private var isChatHistoryEnded: Boolean = false

    init {
        listenNewMessages()
        listenConnectionStatus()
    }

    private fun listenConnectionStatus() {
        authRepository.accountSessionLiveData.observeForever {
            if (it!!.lived) {
                loadedRecipientChatsIds.clear() // Clean initial cache.

                // Reload initial copy
                if (openedChatRecipientId != null) {
                    loadInitialMessages(openedChatRecipientId!!)
                }
            }
        }
    }

    private fun listenNewMessages() {
        protocolClient.listenMessage<NewChatMessageNotifyDTO>("updates.newMessage") {
            val accountId = accountRepository.cachedAccount?.id ?: return@listenMessage
            val recipientId = if (it.peer == accountId) it.sender else it.peer
            val message = ChatMessage(it.id, it.sender, it.peer, it.message, it.date, if (it.peer != accountId) {
                MessageDirection.TO
            } else {
                MessageDirection.FROM
            })

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
        chatDialogNewMessageChannel = ConflatedBroadcastChannel()
    }

    fun endChatDialog() {
        openedChatRecipientId = null
        isChatHistoryEnded = false

        // Close old channel
        if (chatDialogNewMessageChannel != null && !chatDialogNewMessageChannel!!.isClosedForSend) {
            chatDialogNewMessageChannel!!.close()
        }

        if (chatDialogHistoryChannel != null && !chatDialogHistoryChannel!!.isClosedForSend) {
            chatDialogHistoryChannel!!.close()
        }
    }

    fun loadInitialMessages(recipientId: String) {
        // Initial copy not load from database
        if (!loadedRecipientChatsIds.contains(recipientId) && protocolClient.isValid()) {
            loadMessagesFromNetwork(recipientId)
        } else {
            loadMessagesFromDatabase(recipientId)
        }
    }

    fun loadPagedMessages(recipientId: String, offset: Int) {
        if (offset <= 0 || isChatHistoryEnded) return

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
            if (offset == 0) removeSavedMessages(recipientId, false)
        } else {
            if (offset == 0) {
                chatDialogHistoryChannel?.sendBlocking(Pair(ChatLoadingType.INITIAL, messages))
            } else {
                chatDialogHistoryChannel?.sendBlocking(Pair(ChatLoadingType.PAGING, messages))
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
                } else if (it.error == Errors.INVALID_USER) {
                    isChatHistoryEnded = true
                }
            } else if (it.messages.isEmpty()) {
                if (offset == 0) removeSavedMessages(recipientId)
            } else {
                // Message for storing
                val messages = toStorableMessages(it)

                // Save messages into database (for offline mode supporting)
                chatMessagesDao.insertAll(messages)

                // Cache offset
                updateCachedOffset(recipientId, offset)

                // offset == 0 => first initializing
                if (offset == 0) {
                    chatDialogHistoryChannel?.sendBlocking(Pair(ChatLoadingType.INITIAL, messages))
                } else {
                    chatDialogHistoryChannel?.sendBlocking(Pair(ChatLoadingType.PAGING, messages))
                }
            }
        }
    }

    fun sendTextMessage(recipientId: String, text: String) {
        protocolClient.makeRequest<SendChatMessageDTO>("chats.sendMessage", SendChatMessageDTO().apply {
            this.peerId = recipientId
            this.message = formatMessage(text)
        }) {
            if (it.containsError()) return@makeRequest

            // Map to the database format
            val accountId = accountRepository.cachedAccount?.id ?: return@makeRequest
            val message = ChatMessage(it.id, accountId, recipientId, text, it.date, MessageDirection.TO)

            // Save to database
            chatMessagesDao.insertOne(message)

            // Notify global subscribers (dialogs, notifications, etc ...)
            globalNewMessagesChannel.sendBlocking(message)

            // Notify dialog subscriber
            if (openedChatRecipientId == recipientId) {
                chatDialogNewMessageChannel?.sendBlocking(message)
            }
        }
    }

    private fun updateCachedOffset(recipientId: String, offset: Int) {
        loadedRecipientChatsIds[recipientId] = offset // Allow load from this offset
    }

    private fun removeSavedMessages(recipientId: String, removeFromDb: Boolean = true) {
        loadedRecipientChatsIds.minusAssign(recipientId)

        // Экономим на запросах к БД.
        if (removeFromDb) chatMessagesDao.removeAll(recipientId)
    }

    private fun toStorableMessages(chatHistoryDTO: ChatHistoryDTO): List<ChatMessage> {
        return chatHistoryDTO.messages.map {
            ChatMessage(it.id, it.sender, it.peer, it.message, it.date, if (it.peer != accountRepository.cachedAccount?.id) {
                MessageDirection.TO
            } else {
                MessageDirection.FROM
            })
        }
    }
}