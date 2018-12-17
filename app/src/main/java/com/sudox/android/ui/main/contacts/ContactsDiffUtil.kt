package com.sudox.android.ui.main.contacts

import android.support.v7.util.DiffUtil
import com.sudox.android.data.database.model.user.User

class ContactsDiffUtil(val newContacts: ArrayList<User>,
                       val oldContacts: ArrayList<User>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newContacts[newItemPosition].uid == oldContacts[oldItemPosition].uid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newContacts[newItemPosition] == oldContacts[oldItemPosition]
    }

    override fun getOldListSize(): Int = oldContacts.size
    override fun getNewListSize(): Int = newContacts.size
}