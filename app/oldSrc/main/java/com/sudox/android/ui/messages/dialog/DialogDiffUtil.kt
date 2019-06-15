package com.sudox.android.ui.messages.dialog

import androidx.recyclerview.widget.DiffUtil
import com.sudox.android.data.database.model.messages.DialogMessage

class DialogDiffUtil(val newMessages: List<DialogMessage>,
                     val oldMessages: List<DialogMessage>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newMessages[newItemPosition].lid == oldMessages[oldItemPosition].lid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newMessages[newItemPosition] == oldMessages[oldItemPosition]
    }

    override fun getOldListSize(): Int = oldMessages.size
    override fun getNewListSize(): Int = newMessages.size
}