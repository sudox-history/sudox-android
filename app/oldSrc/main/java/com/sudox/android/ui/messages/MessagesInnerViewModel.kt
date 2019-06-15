package com.sudox.android.ui.messages

import androidx.lifecycle.ViewModel
import com.sudox.android.data.repositories.users.AuthRepository
import com.sudox.android.data.repositories.messages.dialogs.DialogsMessagesRepository
import com.sudox.android.data.repositories.users.AccountRepository
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class MessagesInnerViewModel @Inject constructor(val dialogsMessagesRepository: DialogsMessagesRepository,
                                                 private val protocolClient: ProtocolClient,
                                                 private val accountRepository: AccountRepository,
                                                 private val authRepository: AuthRepository): ViewModel()
