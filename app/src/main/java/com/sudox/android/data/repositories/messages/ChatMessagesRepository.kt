package com.sudox.android.data.repositories.messages

import com.sudox.android.data.database.dao.ChatMessagesDao
import com.sudox.android.data.database.model.ChatMessage
import com.sudox.android.data.models.Errors
import com.sudox.android.data.models.chats.ChatLoadingType
import com.sudox.android.data.models.chats.dto.ChatHistoryDTO
import com.sudox.android.data.models.chats.dto.NewChatMessageNotifyDTO
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

const val MESSAGE_TO = 0
const val MESSAGE_FROM = 1

@Singleton
class ChatMessagesRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                                 private val authRepository: AuthRepository,
                                                 private val accountRepository: AccountRepository,
                                                 private val chatMessagesDao: ChatMessagesDao) {

    // ID загруженных чатов ...
    private val loadedRecipientChatsIds = hashSetOf<String>()

    // PublishSubject для доставки новых сообщений.
    val globalNewMessagesChannel: BroadcastChannel<ChatMessage> = ConflatedBroadcastChannel()
    var chatDialogNewMessageChannel: BroadcastChannel<ChatMessage>? = null
    var chatDialogHistoryChannel: BroadcastChannel<Pair<ChatLoadingType, List<ChatMessage>>>? = null
    var openedChatRecipientId: String? = null

    // Защита от десинхронизации данных при скролле (от двух одновременных запросов)
    private var isChatHistoryLoading: Boolean = false
    private var isChatHistoryEnded: Boolean = false

    init {
        listenNewMessages()
    }

    private fun listenNewMessages() {
        protocolClient.listenMessage<NewChatMessageNotifyDTO>("updates.newMessage") {
            val accountId = accountRepository.cachedAccount?.id ?: return@listenMessage
            val recipientId = if (it.peer == accountId) it.sender else it.peer
            val message = ChatMessage(it.id, it.sender, it.peer, it.message, it.date, if (it.peer != accountId) {
                MESSAGE_TO
            } else {
                MESSAGE_FROM
            })

            // Insert into database
            chatMessagesDao.insertOne(message)

            // Notify listeners about new message
            globalNewMessagesChannel.sendBlocking(message)

            // If current dialog active notify subscribers
            if (openedChatRecipientId != null && openedChatRecipientId == recipientId) {
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
        if (openedChatRecipientId == null && chatDialogNewMessageChannel == null) return

        // Remove chat recipient id
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
        if (isChatHistoryLoading) return

        // Initial copy not load from database
        if (!loadedRecipientChatsIds.contains(recipientId) && protocolClient.isValid()) {
            loadMessagesFromNetwork(recipientId)
        } else {
            loadMessagesFromDatabase(recipientId)
        }
    }

    fun loadPagedMessages(recipientId: String, offset: Int) {
        if (offset <= 0 || isChatHistoryLoading || isChatHistoryEnded) return

        // Initial copy loaded from database
        if (!loadedRecipientChatsIds.contains(recipientId)) {
            loadMessagesFromDatabase(recipientId, offset)
        } else {
            loadMessagesFromNetwork(recipientId, offset)
        }
    }

    private fun loadMessagesFromDatabase(recipientId: String, offset: Int = 0) = GlobalScope.launch {
        isChatHistoryLoading = true

        // Load data
        val messages = chatMessagesDao.loadAll(recipientId, offset, 20)

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

        // Unblock!
        isChatHistoryLoading = false
    }

    private fun loadMessagesFromNetwork(recipientId: String, offset: Int = 0) {
        isChatHistoryLoading = true

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

                // offset == 0 => first initializing
                if (offset == 0) {
                    loadedRecipientChatsIds.plusAssign(recipientId)

                    // Notify subscribers
                    chatDialogHistoryChannel?.sendBlocking(Pair(ChatLoadingType.INITIAL, messages))
                } else {
                    chatDialogHistoryChannel?.sendBlocking(Pair(ChatLoadingType.PAGING, messages))
                }
            }

            // Unblock!
            isChatHistoryLoading = false
        }
    }

    private fun removeSavedMessages(recipientId: String, removeFromDb: Boolean = true) {
        loadedRecipientChatsIds.minusAssign(recipientId)

        // Экономим на запросах к БД.
        if (removeFromDb) chatMessagesDao.removeAll(recipientId)
    }

    private fun toStorableMessages(it: ChatHistoryDTO): List<ChatMessage> {
        return it.messages.map {
            ChatMessage(it.id, it.sender, it.peer, it.message, it.date, if (it.peer != accountRepository.cachedAccount?.id) {
                MESSAGE_TO
            } else {
                MESSAGE_FROM
            })
        }
    }
}