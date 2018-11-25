package com.sudox.android.ui.main.messages

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.repositories.messages.chats.DialogsRepository
import javax.inject.Inject

class MessagesViewModel @Inject constructor(private val dialogsRepository: DialogsRepository): ViewModel()