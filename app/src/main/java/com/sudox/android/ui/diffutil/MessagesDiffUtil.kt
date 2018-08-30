package com.sudox.android.ui.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.sudox.android.database.model.Message

class MessagesDiffUtil(private val oldMessagesList: ArrayList<Message>,
                       private val newMessagesList: ArrayList<Message>) :DiffUtil.Callback(){

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newMessage = newMessagesList[newItemPosition]
        val oldMessage = oldMessagesList[oldItemPosition]

        return newMessage.mid == oldMessage.mid
    }


    override fun getOldListSize() = oldMessagesList.size
    override fun getNewListSize() = newMessagesList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newMessage = newMessagesList[newItemPosition]
        val oldMessage = oldMessagesList[oldItemPosition]

        return newMessage == oldMessage
    }
}