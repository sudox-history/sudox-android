package com.sudox.android.ui.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.sudox.android.database.model.Message

class MessagesDiffUtilItem : DiffUtil.ItemCallback<Message>() {

    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return newItem.mid == oldItem.mid
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return newItem == oldItem
    }
}