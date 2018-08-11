package com.sudox.android.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudox.android.R
import com.sudox.android.database.Contact
import kotlinx.android.synthetic.main.card_contact.view.*

class ContactsAdapter(var items: List<Contact>,
                      private val context: Activity) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_contact, parent, false))
    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = items[position].name
        holder.nickname.text = items[position].nickname
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val avatar = view.avatar!!
        val name = view.name!!
        val nickname = view.nickname!!
    }
}
