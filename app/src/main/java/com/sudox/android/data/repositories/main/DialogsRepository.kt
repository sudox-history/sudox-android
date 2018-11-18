package com.sudox.android.data.repositories.main

import android.arch.lifecycle.LiveData
import com.sudox.android.common.userContact
import com.sudox.android.data.database.dao.ChatMessagesDao
import com.sudox.android.data.database.dao.UserDao
import com.sudox.android.data.database.model.ChatMessage
import com.sudox.android.data.database.model.User
import com.sudox.android.data.models.messages.LastMessagesDTO
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.messages.MESSAGE_FROM
import com.sudox.android.data.repositories.messages.MESSAGE_TO
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class DialogsRepository @Inject constructor(val protocolClient: ProtocolClient,
                                            private val authRepository: AuthRepository,
                                            private val accountRepository: AccountRepository,
                                            private val usersRepository: UsersRepository,
                                            private val messagesDao: ChatMessagesDao,
                                            private val userDao: UserDao) {


    val contactsGetLiveData: LiveData<List<User>> = userDao.getUserByType(userContact)


    fun requestLastMessages() {
        protocolClient.makeRequest<LastMessagesDTO>("chats.getChats", LastMessagesDTO().apply {
            limit = 10
            offset = 0
        }) {
            val messages = it.messages.map { chatMessages ->
                ChatMessage(chatMessages.id, chatMessages.sender,
                        chatMessages.peer, chatMessages.message, chatMessages.date,
                        if (chatMessages.peer == accountRepository.cachedAccount!!.id) {
                            MESSAGE_FROM
                        } else {
                            MESSAGE_TO
                        })
            }

            messagesDao.insertAll(messages)
        }
    }

}