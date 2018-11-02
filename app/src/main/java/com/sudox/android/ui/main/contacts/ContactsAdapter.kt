package com.sudox.android.ui.main.contacts

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.R.layout
import com.sudox.android.data.database.model.User
import com.sudox.android.data.models.avatar.AvatarInfo
import com.sudox.android.data.models.avatar.impl.ColorAvatarInfo
import com.sudox.android.data.repositories.main.ContactsRepository
import com.sudox.design.helpers.drawAvatar
import com.sudox.design.helpers.drawCircleBitmap
import com.sudox.design.helpers.getTwoFirstLetters
import com.sudox.design.helpers.setOnItemClickListener
import kotlinx.android.synthetic.main.item_contact.view.*
import javax.inject.Inject

class ContactsAdapter @Inject constructor(val context: Context,
                                          private val contactsRepository: ContactsRepository) : RecyclerView.Adapter<ContactsAdapter.Holder>() {

    var items: List<User> = arrayListOf()

    // Кэллбэки
    lateinit var clickCallback: (User) -> (Unit)
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

        // Bind data
        holder.bindData(contact)
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val avatar = view.avatar!!
        val name = view.name!!
        val nickname = view.nickname!!

        fun bindData(user: User) {
            bindAvatar(user)

            // Bind others data ...
            name.text = user.name
            nickname.text = user.nickname
        }

        private fun bindAvatar(user: User) {
            val avatarInfo = AvatarInfo.parse(user.avatar)

            // aka GradientAvatar
            if (avatarInfo is ColorAvatarInfo) {
                drawCircleBitmap(view.context, drawAvatar(
                        text = user.name.getTwoFirstLetters(),
                        firstColor = avatarInfo.firstColor,
                        secondColor = avatarInfo.secondColor), avatar)
            }
        }
    }
}