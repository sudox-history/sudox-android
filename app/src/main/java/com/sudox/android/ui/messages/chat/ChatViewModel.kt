package com.sudox.android.ui.messages.chat

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.messages.ChatRepository
import javax.inject.Inject

class ChatViewModel @Inject constructor(val chatRepository: ChatRepository,
                                        val authRepository: AuthRepository) : ViewModel() {
}