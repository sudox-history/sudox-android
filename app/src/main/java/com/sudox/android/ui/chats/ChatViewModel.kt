package com.sudox.android.ui.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.repository.auth.AccountRepository
import com.sudox.android.common.repository.auth.AuthRepository
import com.sudox.android.common.repository.chat.MessagesRepository
import com.sudox.android.database.model.Message
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class ChatViewModel @Inject constructor(private val messagesRepository: MessagesRepository,
                                        private val protocolClient: ProtocolClient,
                                        private val accountRepository: AccountRepository,
                                        private val authRepository: AuthRepository): ViewModel() {

    var connectLiveData: LiveData<ConnectState>

    val newMessageLiveData: LiveData<Message?>

    init {
        protocolClient.nullLiveData()
        connectLiveData = protocolClient.connectionStateLiveData
        messagesRepository.nullLiveData()
        newMessageLiveData = messagesRepository.newMessageLiveData
    }

    fun getFirstMessagesFromServer(id: String) = messagesRepository.getFirstMessagesFromServer(id)

    fun getMessagesFromDB(id: String) = messagesRepository.requestFromDB(id)

    fun sendSimpleMessage(id: String, text: String) = messagesRepository.sendSimpleMessage(id, text)

    fun getAccount() = accountRepository.getAccount()

    fun sendSecret(sudoxAccount: SudoxAccount?) = authRepository.sendSecret(sudoxAccount?.secret, sudoxAccount?.id)

    fun disconnect() = protocolClient.disconnect()

    fun isConnected() = protocolClient.isConnected()


}