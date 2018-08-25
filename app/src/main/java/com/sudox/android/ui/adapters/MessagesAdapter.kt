package com.sudox.android.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudox.android.R
import com.sudox.android.database.model.Message
import kotlinx.android.synthetic.main.textview_message.view.*

class MessagesAdapter(var items: List<Message>,
                      private val context: Activity) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesAdapter.ViewHolder {
        return MessagesAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.textview_message, parent, false))
    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = items[position].text
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text = view.message_text!!

    }
}