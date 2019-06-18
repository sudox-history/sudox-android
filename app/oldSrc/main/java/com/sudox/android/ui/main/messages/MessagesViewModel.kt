package com.sudox.android.ui.main.messages

import androidx.lifecycle.ViewModel
import com.sudox.android.data.repositories.messages.dialogs.DialogsRepository
import javax.inject.Inject

class MessagesViewModel @Inject constructor(private val dialogsRepository: DialogsRepository): ViewModel()