package com.sudox.android.ui.main.messages

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.repositories.messages.dialogs.DialogsRepository
import javax.inject.Inject

class MessagesViewModel @Inject constructor(private val dialogsRepository: DialogsRepository): ViewModel()