package com.sudox.android.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.repository.auth.AccountRepository
import com.sudox.android.common.repository.auth.AuthRepository
import com.sudox.android.common.repository.chat.MessagesRepository
import com.sudox.android.common.repository.main.ContactsRepository
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class MainViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                        private val contactsRepository: ContactsRepository,
                                        private val messagesRepository: MessagesRepository,
                                        private val accountRepository: AccountRepository,
                                        private val authRepository: AuthRepository) : ViewModel() {

    var  connectLiveData: LiveData<ConnectState>
    init {
        protocolClient.nullLiveData()
        connectLiveData = protocolClient.connectionStateLiveData
    }

    fun getAccount() = accountRepository.getAccount()

    fun removeAllData() = accountRepository.deleteData()

    fun initContactsListeners() = contactsRepository.initContactsListeners()

    fun initMessagesListener() = messagesRepository.initMessagesListeners()

    fun removeAllAccounts() = accountRepository.removeAccounts()

    fun sendSecret(sudoxAccount: SudoxAccount?) = authRepository.sendSecret(sudoxAccount?.secret, sudoxAccount?.id)

    fun disconnect() = protocolClient.disconnect()

    fun isConnected() = protocolClient.isConnected()

    fun getAllContactsFromServer() = contactsRepository.getAllContactsFromServer()

    fun getAllContactsFromDB() = contactsRepository.requestAllContactsFromDB()
}