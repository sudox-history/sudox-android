package com.sudox.android.ui.messages.chat

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.data.database.model.messages.ChatMessage
import com.sudox.android.data.models.messages.MessageDirection
import com.sudox.android.data.models.messages.MessageStatus
import kotlinx.android.synthetic.main.textview_message_to.view.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ChatAdapter @Inject constructor(val context: Context) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    internal var messages: ArrayList<ChatMessage> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MessageDirection.TO.ordinal) {
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.textview_message_to, parent, false))
        } else {
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.textview_message_from, parent, false))
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = messages[position].message
        holder.text.setCompoundDrawablesWithIntrinsicBounds(null, null, when {
            messages[position].status == MessageStatus.IN_DELIVERY -> ContextCompat.getDrawable(context, R.drawable.ic_clock)
            messages[position].status == MessageStatus.DELIVERED -> ContextCompat.getDrawable(context, R.drawable.ic_check)
            messages[position].status == MessageStatus.NOT_DELIVERED -> ContextCompat.getDrawable(context, R.drawable.ic_error)
            else -> TODO("")
        }, null)
        holder.time.text = DateFormat.format("HH:mm", Date(messages[position].date)).toString()
    }

    override fun getItemViewType(position: Int) = messages[position].direction.ordinal
    override fun getItemCount() = messages.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text = view.message_text!!
        val time = view.time_text!!
    }
}