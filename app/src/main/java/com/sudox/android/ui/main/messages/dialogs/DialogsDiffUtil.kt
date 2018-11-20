package com.sudox.android.ui.main.messages.dialogs

import android.support.v7.util.DiffUtil
import com.sudox.android.data.database.model.ChatMessage
import com.sudox.android.data.database.model.User

class DialogsDiffUtil(val oldDialogs: List<Pair<User, ChatMessage>>,
                      val newDialogs: List<Pair<User, ChatMessage>>) : DiffUtil.Callback() {

    override fun areItemsTheSame(positionOld: Int, positionNew: Int): Boolean {
        val oldDialog = oldDialogs[positionOld]
        val oldDialogUser = oldDialog.first
        val newDialog = newDialogs[positionNew]
        val newDialogUser = newDialog.first

        return oldDialogUser.uid == newDialogUser.uid
    }

    override fun areContentsTheSame(positionOld: Int, positionNew: Int): Boolean {
        val oldDialog = oldDialogs[positionOld]
        val oldDialogUser = oldDialog.first
        val oldDialogMessage = oldDialog.second
        val newDialog = newDialogs[positionNew]
        val newDialogUser = newDialog.first
        val newDialogMessage = newDialog.second

        return oldDialogUser == newDialogUser && oldDialogMessage == newDialogMessage
    }

    override fun getOldListSize() = oldDialogs.size
    override fun getNewListSize() = newDialogs.size
}