package com.sudox.android.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudox.android.R
import com.sudox.android.common.repository.chat.MESSAGE_TO
import com.sudox.android.database.model.Message
import kotlinx.android.synthetic.main.textview_message_to.view.*
import java.util.*


class MessagesAdapter(var items: ArrayList<Message>,
                      private val context: Activity) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {


    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesAdapter.ViewHolder {
        return if(viewType == MESSAGE_TO)
            MessagesAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.textview_message_to, parent, false))
        else
            MessagesAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.textview_message_from, parent, false))
    }



    override fun getItemCount(): Int {
       return items.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = items[position].text

        val dateString = DateFormat.format("HH:mm", Date(items[position].time)).toString()
        holder.time.text = dateString
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text = view.message_text!!
        val time = view.time_text!!
    }
}