package com.sudox.android.ui.diffutil

import android.support.v7.util.DiffUtil
import com.sudox.android.database.model.Contact

class ContactsDiffUtil(private val newContactsList: List<Contact>,
                       private val oldContactsList: List<Contact>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newContact = newContactsList[newItemPosition]
        val oldContact = oldContactsList[oldItemPosition]

        return newContact.cid == oldContact.cid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newContact = newContactsList[newItemPosition]
        val oldContact = oldContactsList[oldItemPosition]

        return newContact == oldContact
    }

    override fun getOldListSize() = oldContactsList.size
    override fun getNewListSize() = newContactsList.size
}