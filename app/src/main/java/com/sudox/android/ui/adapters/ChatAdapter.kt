package com.sudox.android.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.data.database.model.ChatMessage
import kotlinx.android.synthetic.main.textview_message_to.view.*
import android.text.format.DateFormat
import com.sudox.android.data.repositories.messages.MESSAGE_TO
import java.util.*

class ChatAdapter(var items: List<ChatMessage>,
                  private val context: Activity) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        return if (viewType == MESSAGE_TO)
            ChatAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.textview_message_to, parent, false))
        else
            ChatAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.textview_message_from, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = items[position].message
        holder.time.text = DateFormat.format("HH:mm", Date(items[position].date)).toString()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text = view.message_text!!
        val time = view.time_text!!
    }
}