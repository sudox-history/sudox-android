package com.sudox.android.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.sudox.android.R
import com.sudox.android.common.helpers.drawContactAvatar
import com.sudox.android.data.database.model.Contact
import kotlinx.android.synthetic.main.card_contact.view.*
import javax.inject.Inject

class ContactsAdapter @Inject constructor(val context: Context) : RecyclerView.Adapter<ContactsAdapter.Holder>() {

    lateinit var items: ArrayList<Contact>
    lateinit var clickCallback: (Contact) -> (Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater
                .from(context)
                .inflate(R.layout.card_contact, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val contact = items[position]

        // Draw avatar
        if (contact.firstColor != null && contact.secondColor != null) {
            Glide.with(context)
                    .load(drawContactAvatar(contact))
                    .into(holder.avatar)
        } else {
            // TODO: Photo avatar
        }

        // Setting click listener
        holder.itemView.apply {
            setOnClickListener { clickCallback(contact) }
            // TODO: Long click handle
        }

        // Bind data
        holder.bind(contact)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar = view.avatar!!
        val name = view.name!!
        val nickname = view.nickname!!

        fun bind(contact: Contact) {
            name.text = contact.name
            nickname.text = contact.nickname
        }
    }
}