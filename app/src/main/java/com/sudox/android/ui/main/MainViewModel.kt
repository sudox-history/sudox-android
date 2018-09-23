package com.sudox.android.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
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

    val connectionStateLiveData = protocolClient.connectionStateLiveData

    fun getAccount() = accountRepository.getAccount()

//    fun logOut() = authRepository.logOut()

    fun initContactsListeners() = contactsRepository.initContactsListeners()

    fun initMessagesListener() = messagesRepository.initMessagesListener()

    fun removeAllAccounts() = accountRepository.removeAccounts()

//    fun setSecret(sudoxAccount: SudoxAccount?) = Unit //authRepository.setSecret(sudoxAccount?.secret, sudoxAccount?.id)

    fun disconnect() = protocolClient.close()

    fun isConnected() = protocolClient.isConnected()

//    fun getAllContactsFromServer() = contactsRepository.getAllContactsFromServer()

    fun getAllContactsFromDB() = contactsRepository.requestAllContactsFromDB()
}