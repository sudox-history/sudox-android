package com.sudox.android.ui.main.contacts

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.data.database.model.user.User
import com.sudox.design.helpers.formatHtml
import kotlinx.android.synthetic.main.item_contact.view.*
import javax.inject.Inject

class ContactsAdapter @Inject constructor(val context: Context) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    // Contacts for showing
    var contacts: ArrayList<User> = arrayListOf()

    override fun onCreateViewHolder(root: ViewGroup, type: Int): ViewHolder {
        return ViewHolder(LayoutInflater
                .from(context)
                .inflate(R.layout.item_contact, root, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val contact = contacts[position]
        val nicknameParts = contact.separateNickname()

        // Bind data
        viewHolder.avatar.bindUser(contact)
        viewHolder.name.text = contact.name
        viewHolder.nickname.text = formatHtml(context.getString(R.string.nickname_format, nicknameParts[0], nicknameParts[1]))
    }

    override fun getItemCount(): Int = contacts.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var avatar = itemView.contactAvatar
        var name = itemView.contactName
        var nickname = itemView.contactNickname
    }
}