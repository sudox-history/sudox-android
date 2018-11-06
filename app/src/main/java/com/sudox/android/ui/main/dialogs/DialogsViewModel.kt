package com.sudox.android.ui.main.dialogs

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.repositories.main.DialogsRepository
import javax.inject.Inject

class DialogsViewModel @Inject constructor(private val dialogsRepository: DialogsRepository): ViewModel()