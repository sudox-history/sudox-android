package com.sudox.android.ui.main.contacts

import android.support.v7.util.DiffUtil
import com.sudox.android.data.database.model.user.User

class ContactsDiffUtil(private val newContactsList: List<User>,
                       private val oldContactsList: List<User>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newContact = newContactsList[newItemPosition]
        val oldContact = oldContactsList[oldItemPosition]

        return newContact.uid == oldContact.uid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newContact = newContactsList[newItemPosition]
        val oldContact = oldContactsList[oldItemPosition]

        return newContact == oldContact
    }

    override fun getOldListSize() = oldContactsList.size
    override fun getNewListSize() = newContactsList.size
}