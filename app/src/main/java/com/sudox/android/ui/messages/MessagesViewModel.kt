package com.sudox.android.ui.messages

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.chat.ChatRepository
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class MessagesViewModel @Inject constructor(val chatRepository: ChatRepository,
                                            private val protocolClient: ProtocolClient,
                                            private val accountRepository: AccountRepository,
                                            private val authRepository: AuthRepository): ViewModel() {
}