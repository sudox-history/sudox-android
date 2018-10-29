package com.sudox.android.data.repositories.chat

import android.arch.lifecycle.LiveData
import com.sudox.android.data.database.dao.UserChatMessagesDao
import com.sudox.android.data.database.model.UserChatMessage
import com.sudox.android.data.models.chats.dto.SendUserChatMessageDTO
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject
import javax.inject.Singleton

const val MESSAGE_TO = 0
const val MESSAGE_FROM = 1

@Singleton
class ChatRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                         private val userChatMessagesDao: UserChatMessagesDao) {

    init {
//        protocolClient.listenMessage<>()
    }

    fun sendSimpleMessage(peerId: String, message: String) {
        protocolClient.makeRequest<SendUserChatMessageDTO>("chats.send", SendUserChatMessageDTO().apply {
            this.peerId = peerId
            this.message = message
        }) {
            if (it.containsError()) return@makeRequest

            // Save messages to database
            userChatMessagesDao.insertOne(UserChatMessage(it.messageId, message, it.date, MESSAGE_TO, peerId))
        }
    }

    fun observeWithCurrentRecipient(recipientId: String): LiveData<List<UserChatMessage>>
            = userChatMessagesDao.loadMessagesWithRecipient(recipientId)
}