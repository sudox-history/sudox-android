package com.sudox.android.ui.main.contacts

import androidx.lifecycle.MutableLiveData
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.repositories.users.ContactsRepository
import com.sudox.design.avatar.AvatarView
import com.sudox.design.helpers.formatHtml
import com.sudox.design.helpers.setOnItemClickListener
import com.sudox.design.widgets.PrecomputedTextView
import kotlinx.android.synthetic.main.item_contact.view.*
import kotlinx.android.synthetic.main.item_contacts_count.view.*
import javax.inject.Inject

class ContactsAdapter @Inject constructor(val context: Context,
                                          val contactsRepository: ContactsRepository) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    // Contacts for showing
    var contacts: ArrayList<User> = arrayListOf()
    val editableContactsLiveData: MutableLiveData<User> = MutableLiveData()
    val clickedContactLiveData: MutableLiveData<User> = MutableLiveData()

    companion object {
        const val CONTACTS_LIST_CONTACT = 0
        const val CONTACTS_LIST_CONTACTS_COUNT = 1
    }

    // Menu inflater for context menu (must be initialized in onViewCreated())
    lateinit var menuInflater: MenuInflater

    override fun onCreateViewHolder(root: ViewGroup, type: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        if (type == CONTACTS_LIST_CONTACT) {
            val view = LayoutInflater
                    .from(context)
                    .inflate(R.layout.item_contact, root, false)

            val holder = ContactViewHolder(view)

            // Bind listeners
            view.setOnLongClickListener { it.showContextMenu() }
            view.setOnClickListener {
                if (holder.adapterPosition != NO_POSITION)
                    clickedContactLiveData.postValue(contacts[holder.adapterPosition])
            }

            view.setOnCreateContextMenuListener { menu, _, _ ->
                menuInflater.inflate(R.menu.menu_contact_context, menu)

                // Слушаем события
                menu.setOnItemClickListener {
                    if (holder.adapterPosition != NO_POSITION) {
                        when (it.itemId) {
                            R.id.contact_remove_item -> contactsRepository.removeContact(contacts[holder.adapterPosition].uid)
                            R.id.contact_edit_item -> editableContactsLiveData.postValue(contacts[holder.adapterPosition])
                        }

                        return@setOnItemClickListener true
                    }

                    return@setOnItemClickListener false
                }
            }

            return holder
        } else {
            val view = LayoutInflater
                    .from(context)
                    .inflate(R.layout.item_contacts_count, root, false)

            return ContactsCountHolder(view)
        }
    }

    @Suppress("NAME_SHADOWING")
    override fun onBindViewHolder(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val type = getItemViewType(position)

        if (type == CONTACTS_LIST_CONTACT) {
            val contact = contacts[position]
            val nicknameParts = contact.separateNickname()
            val viewHolder = viewHolder as ContactViewHolder

            // Bind data
            viewHolder.avatar.bindUser(contact)
            viewHolder.name.installText(contact.name)
            viewHolder.nickname.installText(formatHtml(context.getString(R.string.nickname_format, nicknameParts[0], nicknameParts[1])))
        } else {
            val viewHolder = viewHolder as ContactsCountHolder
            val countText = context.resources.getQuantityString(R.plurals.contacts_count, contacts.size, contacts.size)

            // Bind data
            viewHolder.count.installText(countText)
        }
    }

    override fun getItemCount(): Int = if (contacts.isNotEmpty()) contacts.size + 1 else 0

    override fun getItemViewType(position: Int): Int = if (position > contacts.lastIndex && contacts.isNotEmpty()) {
        CONTACTS_LIST_CONTACTS_COUNT
    } else {
        CONTACTS_LIST_CONTACT
    }

    class ContactViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var avatar: AvatarView = itemView.contactAvatar
        var name: PrecomputedTextView = itemView.contactName
        var nickname: PrecomputedTextView = itemView.contactNickname
    }

    class ContactsCountHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val count: PrecomputedTextView = itemView.contactsCount
    }
}