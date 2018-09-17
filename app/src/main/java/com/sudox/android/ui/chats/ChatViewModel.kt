package com.sudox.android.ui.chats

import android.arch.lifecycle.ViewModel
import com.sudox.android.common.repository.auth.AccountRepository
import com.sudox.android.common.repository.auth.AuthRepository
import com.sudox.android.common.repository.chat.MessagesRepository
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class ChatViewModel @Inject constructor(val messagesRepository: MessagesRepository,
                                        private val protocolClient: ProtocolClient,
                                        private val accountRepository: AccountRepository,
                                        private val authRepository: AuthRepository): ViewModel() {

    val connectLiveData = protocolClient.connectionStateLiveData
    val messagesLiveData = messagesRepository.messagesLiveData
    val loadedContactsIds = messagesRepository.loadedContactsIds

    fun loadHistoryIntoDatabase(contactId: String, offset: Int, limit: Int)
            = messagesRepository.loadHistoryIntoDatabase(contactId, offset, limit)

    fun loadHistoryFromDatabase(contactId: String)
            = messagesRepository.loadHistoryFromDatabase(contactId)

    fun sendTextMessage(contactId: String, text: String)
            = messagesRepository.sendTextMessage(contactId, text)

    fun disconnect() = protocolClient.close()
}