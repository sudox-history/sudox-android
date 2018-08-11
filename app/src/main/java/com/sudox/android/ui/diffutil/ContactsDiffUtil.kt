package com.sudox.android.ui.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.sudox.android.database.Contact

class ContactsDiffUtil(val newContactsList: List<Contact>,
                       val oldContactsList: List<Contact>) : DiffUtil.Callback() {

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