package com.sudox.android.ui.main.contacts

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.repositories.main.ContactsRepository
import com.sudox.design.avatar.AvatarView
import com.sudox.design.helpers.formatHtml
import com.sudox.design.helpers.setOnItemClickListener
import com.sudox.design.widgets.PrecomputedTextView
import kotlinx.android.synthetic.main.item_contact.view.*
import javax.inject.Inject

class ContactsAdapter @Inject constructor(val context: Context,
                                          val contactsRepository: ContactsRepository) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    // Contacts for showing
    var contacts: ArrayList<User> = arrayListOf()
    val editableContactsLiveData: MutableLiveData<User> = MutableLiveData()
    val clickedContactLiveData: MutableLiveData<User> = MutableLiveData()

    // Menu inflater for context menu (must be initialized in onViewCreated())
    lateinit var menuInflater: MenuInflater

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
        viewHolder.name.installText(contact.name)
        viewHolder.nickname.installText(formatHtml(context.getString(R.string.nickname_format, nicknameParts[0], nicknameParts[1])))
        viewHolder.itemView.setOnLongClickListener { it.showContextMenu() }
        viewHolder.itemView.setOnClickListener { clickedContactLiveData.postValue(contact) }
        viewHolder.itemView.setOnCreateContextMenuListener { menu, _, _ ->
            menuInflater.inflate(R.menu.menu_contact_context, menu)

            // Слушаем события
            menu.setOnItemClickListener {
                when (it.itemId) {
                    R.id.contact_remove_item -> contactsRepository.removeContact(contact.uid)
                    R.id.contact_edit_item -> editableContactsLiveData.postValue(contact)
                }

                return@setOnItemClickListener true
            }
        }
    }

    override fun getItemCount(): Int = contacts.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var avatar: AvatarView = itemView.contactAvatar
        var name: PrecomputedTextView = itemView.contactName
        var nickname: PrecomputedTextView = itemView.contactNickname
    }
}