package com.sudox.android.ui.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.sudox.android.R
import com.sudox.android.common.helpers.drawContactAvatar
import com.sudox.android.data.database.model.Contact
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.android.synthetic.main.card_contact.view.*

class ContactsAdapter(var items: List<Contact>,
                      private val context: Activity) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    val clickedLongContactLiveData = SingleLiveEvent<String>()
    val clickedSimpleContactLiveData = SingleLiveEvent<Contact>()

    var isBlocked = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_contact, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get contact from list by position
        val contact = items[position]

        // Setting image
        if (contact.firstColor != null && contact.secondColor != null) {
            Glide.with(context).load(drawContactAvatar(contact)).into(holder.avatar)
        } else {
            TODO("if photo is not gradient")
        }

        // Set on click listener by using RxJava
        holder.itemView.setOnLongClickListener {
            clickedLongContactLiveData.postValue(contact.cid)
            return@setOnLongClickListener true
        }

        holder.itemView.setOnClickListener {
            if (!isBlocked) {
                isBlocked = true
                clickedSimpleContactLiveData.postValue(contact)
            }
        }

        // Setting name
        holder.name.text = contact.name
        holder.nickname.text = contact.nickname
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar = view.avatar!!
        val name = view.name!!
        val nickname = view.nickname!!
    }
}
