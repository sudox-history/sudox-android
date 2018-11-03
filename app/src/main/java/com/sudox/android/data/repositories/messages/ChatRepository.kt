package com.sudox.android.data.repositories.messages

import com.sudox.android.data.database.dao.ChatMessagesDao
import com.sudox.android.data.database.model.ChatMessage
import com.sudox.android.data.models.chats.dto.ChatHistoryDTO
import com.sudox.android.data.models.chats.dto.NewChatMessageNotifyDTO
import com.sudox.android.data.models.chats.dto.SendChatMessageDTO
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import javax.inject.Inject
import javax.inject.Singleton

const val MESSAGE_TO = 0
const val MESSAGE_FROM = 1

const val CHAT_MESSAGES_INITIAL_SIZE_DATABASE = 100
const val CHAT_MESSAGES_INITIAL_SIZE = 30
const val CHAT_MESSAGES_SIZE = 30

@Singleton
class ChatRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                         private val authRepository: AuthRepository,
                                         private val accountRepository: AccountRepository,
                                         private val chatMessagesDao: ChatMessagesDao) {

    private val loadedPeerChatsIds: HashSet<String> = hashSetOf()
    val newMessageLiveData: SingleLiveEvent<ChatMessage> = SingleLiveEvent()

    init {
        protocolClient.listenMessage<NewChatMessageNotifyDTO>("notify.chats.new") {
            if (accountRepository.cachedAccount == null) return@listenMessage

            val message = ChatMessage(it.id, it.sender, it.peer, it.message, it.date, if (it.peer == accountRepository.cachedAccount!!.id) {
                MESSAGE_FROM
            } else {
                MESSAGE_TO
            })

            chatMessagesDao.insertOne(message)
            newMessageLiveData.postValue(message)
            chatMessagesDao.removeOldMessages(if (it.peer == accountRepository.cachedAccount!!.id) {
                it.sender
            } else {
                it.peer
            })
        }

        authRepository.accountSessionLiveData.observeForever {
            if (it!!.lived) loadedPeerChatsIds.clear()
        }
    }

    fun getInitialHistory(peerId: String,
                          messagesCallback: (List<ChatMessage>) -> Unit,
                          errorCallback: (Int) -> Unit) = GlobalScope.async {

        if (protocolClient.isValid()) {
            if (!loadedPeerChatsIds.contains(peerId)) {
                protocolClient.makeRequest<ChatHistoryDTO>("chats.getHistory", ChatHistoryDTO().apply {
                    this.id = peerId
                    this.offset = 0
                    this.limit = CHAT_MESSAGES_INITIAL_SIZE
                }) {
                    if (!it.containsError()) {
                        val messages = it.messages.map {
                            ChatMessage(it.id, it.sender, it.peer, it.message, it.date,
                                    if (it.peer == accountRepository.cachedAccount!!.id) {
                                        MESSAGE_FROM
                                    } else {
                                        MESSAGE_TO
                                    })
                        }

                        // Return result
                        messagesCallback(messages)

                        // Save to database & validate cache for this peer
                        chatMessagesDao.removeAll(peerId)
                        chatMessagesDao.insertAll(messages)
                        loadedPeerChatsIds.add(peerId)
                    } else {
                        errorCallback(it.error)
                    }
                }
            } else {
                messagesCallback(chatMessagesDao.loadAll(peerId, 0, CHAT_MESSAGES_INITIAL_SIZE_DATABASE))
            }
        } else {
            messagesCallback(chatMessagesDao.loadAll(peerId, 0, CHAT_MESSAGES_INITIAL_SIZE_DATABASE))
        }
    }

    fun getHistory(peerId: String,
                   offset: Int,
                   messagesCallback: (List<ChatMessage>) -> Unit,
                   errorCallback: (Int) -> Unit) = GlobalScope.async {

        if (protocolClient.isValid()) {
            protocolClient.makeRequest<ChatHistoryDTO>("chats.getHistory", ChatHistoryDTO().apply {
                this.id = peerId
                this.offset = offset
                this.limit = CHAT_MESSAGES_SIZE
            }) {
                if (!it.containsError()) {
                    val messages = it.messages.map {
                        ChatMessage(it.id, it.sender, it.peer, it.message, it.date,
                                if (it.peer == accountRepository.cachedAccount!!.id) {
                                    MESSAGE_FROM
                                } else {
                                    MESSAGE_TO
                                })
                    }

                    // Return result
                    messagesCallback(messages)
                } else {
                    errorCallback(it.error)
                }
            }
        }
    }

    fun sendSimpleMessage(peerId: String, message: String) {
        protocolClient.makeRequest<SendChatMessageDTO>("chats.send", SendChatMessageDTO().apply {
            this.peerId = peerId
            this.message = message
        }) {
            if (it.containsError()) return@makeRequest
            if (accountRepository.cachedAccount == null) return@makeRequest

            val chatMessage =
                    ChatMessage(it.messageId, accountRepository.cachedAccount!!.id, peerId, message, it.date, MESSAGE_TO)

            // Save messages to database
            chatMessagesDao.insertOne(chatMessage)
            newMessageLiveData.postValue(chatMessage)
            chatMessagesDao.removeOldMessages(peerId)
        }
    }
}