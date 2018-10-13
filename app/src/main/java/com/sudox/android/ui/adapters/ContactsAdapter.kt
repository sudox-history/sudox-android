package com.sudox.android.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.R.layout
import com.sudox.android.common.helpers.drawAvatar
import com.sudox.android.common.helpers.drawCircleBitmap
import com.sudox.android.common.helpers.setOnItemClickListener
import com.sudox.android.data.database.model.Contact
import com.sudox.android.data.models.avatar.AvatarInfo
import com.sudox.android.data.models.avatar.impl.ColorAvatarInfo
import com.sudox.android.data.repositories.main.ContactsRepository
import kotlinx.android.synthetic.main.item_contact.view.*
import javax.inject.Inject

class ContactsAdapter @Inject constructor(val context: Context,
                                          val contactsRepository: ContactsRepository) : RecyclerView.Adapter<ContactsAdapter.Holder>() {

    var items: List<Contact> = arrayListOf()

    // Кэллбэки
    lateinit var clickCallback: (Contact) -> (Unit)
    lateinit var menuInflater: MenuInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater
                .from(context)
                .inflate(layout.item_contact, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val contact = items[position]

        // Set listeners
        holder.itemView.apply {
            setOnClickListener { clickCallback(contact) }
            setOnLongClickListener { showContextMenu() }
            setOnCreateContextMenuListener { menu, _, _ ->
                run {
                    menuInflater.inflate(R.menu.menu_contact_options, menu)

                    // Свой способ :)
                    menu.setOnItemClickListener {
                        when (it.itemId) {
                            R.id.remove_contact -> contactsRepository.removeContact(contact.uid)
                        }

                        // Все норм
                        return@setOnItemClickListener true
                    }
                }
            }
        }

        // Bind data
        holder.bindData(contact)
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val avatar = view.avatar!!
        val name = view.name!!
        val nickname = view.nickname!!

        fun bindData(contact: Contact) {
            bindAvatar(contact)

            // Bind others data ...
            name.text = contact.name
            nickname.text = contact.nickname
        }

        private fun bindAvatar(contact: Contact) {
            val avatarInfo = AvatarInfo.parse(contact.photo)

            // aka GradientAvatar
            if (avatarInfo is ColorAvatarInfo) {
                drawCircleBitmap(view.context, drawAvatar(
                        text = contact.buildShortName(),
                        firstColor = avatarInfo.firstColor,
                        secondColor = avatarInfo.secondColor), avatar)
            }
        }
    }
}