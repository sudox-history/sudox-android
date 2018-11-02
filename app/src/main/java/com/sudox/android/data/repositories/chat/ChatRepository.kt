package com.sudox.android.data.repositories.chat

import android.arch.lifecycle.LiveData
import com.sudox.android.data.database.dao.ChatMessagesDao
import com.sudox.android.data.database.model.ChatMessage
import com.sudox.android.data.models.chats.dto.NewChatMessageNotifyDTO
import com.sudox.android.data.models.chats.dto.SendChatMessageDTO
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import javax.inject.Inject
import javax.inject.Singleton

const val MESSAGE_TO = 0
const val MESSAGE_FROM = 1

@Singleton
class ChatRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                         private val authRepository: AuthRepository,
                                         private val accountRepository: AccountRepository,
                                         private val chatMessagesDao: ChatMessagesDao) {

    private var senderId: String? = null

    init {
        authRepository.accountSessionLiveData.observeForever {
            GlobalScope.async {
                senderId = if (it != null) {
                    accountRepository.getAccount().await()!!.id
                } else {
                    null
                }
            }
        }

        protocolClient.listenMessage<NewChatMessageNotifyDTO>("notify.chats.new") {
            chatMessagesDao.insertOne(ChatMessage(it.id, it.sender, it.peer, it.message, it.date, if (it.peer == senderId) {
                MESSAGE_FROM
            } else {
                MESSAGE_TO
            }))
        }
    }

    fun sendSimpleMessage(peerId: String, message: String) {
        protocolClient.makeRequest<SendChatMessageDTO>("chats.send", SendChatMessageDTO().apply {
            this.peerId = peerId
            this.message = message
        }) {
            if (it.containsError()) return@makeRequest

            // Save messages to database
            chatMessagesDao.insertOne(ChatMessage(it.messageId, senderId!!, it.peerId, message, it.date, MESSAGE_TO))
        }
    }

    fun observeWithCurrentRecipient(recipientId: String): LiveData<List<ChatMessage>>
            = chatMessagesDao.loadMessagesByPeer(recipientId)
}