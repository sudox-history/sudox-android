package com.sudox.android.ui.messages.dialog

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.data.database.model.messages.DialogMessage
import com.sudox.android.data.models.messages.MessageDirection
import com.sudox.android.data.models.messages.MessageStatus
import kotlinx.android.synthetic.main.view_dialog_message_to.view.*
import java.util.*

class DialogAdapter(val context: Context) : RecyclerView.Adapter<DialogAdapter.ViewHolder>() {

    var messages: ArrayList<DialogMessage> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, type: Int) = ViewHolder(
            LayoutInflater
                    .from(context)
                    .inflate(if (type == MessageDirection.TO.ordinal) {
                        R.layout.view_dialog_message_to
                    } else {
                        R.layout.view_dialog_message_from
                    }, parent, false))

    override fun onBindViewHolder(holder: DialogAdapter.ViewHolder, position: Int) {
        val message = messages[position]
        var bottomMargin = 2F * context.resources.displayMetrics.density

        if (position < messages.lastIndex && message.direction != messages[position + 1].direction) {
            bottomMargin = 6 * context.resources.displayMetrics.density
            holder.dialogMessageEndOfGroupMark.visibility = View.VISIBLE
            holder.dialogMessageContaienr.background = if (message.direction == MessageDirection.TO) {
                ContextCompat.getDrawable(context, R.drawable.shape_message_to_end)
            } else {
                ContextCompat.getDrawable(context, R.drawable.shape_message_from_end)
            }
        } else if (position == messages.lastIndex) {
            holder.dialogMessageEndOfGroupMark.visibility = View.VISIBLE
            holder.dialogMessageContaienr.background = if (message.direction == MessageDirection.TO) {
                ContextCompat.getDrawable(context, R.drawable.shape_message_to_end)
            } else {
                ContextCompat.getDrawable(context, R.drawable.shape_message_from_end)
            }
        } else if (position < messages.lastIndex && message.direction == messages[position + 1].direction) {
            holder.dialogMessageEndOfGroupMark.visibility = View.INVISIBLE
            holder.dialogMessageContaienr.background = if (message.direction == MessageDirection.TO) {
                ContextCompat.getDrawable(context, R.drawable.shape_message_to)
            } else {
                ContextCompat.getDrawable(context, R.drawable.shape_message_from)
            }
        }

        // Update margin
        holder.itemView.layoutParams = (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams)
                .apply { this.bottomMargin = bottomMargin.toInt() }

        holder.dialogMessageText.installText(message.message)
        holder.dialogMessageSendTime.installText(DateFormat.format("HH:mm", Date(message.date)).toString())

        if (message.direction == MessageDirection.TO) {
            holder.dialogMessageSendStatus.setBackgroundResource(when {
                message.status ==  MessageStatus.IN_DELIVERY -> R.drawable.ic_clock
                message.status == MessageStatus.DELIVERED -> R.drawable.ic_check
                else -> R.drawable.ic_error
            })
        }
    }

    override fun getItemCount() = messages.size
    override fun getItemViewType(position: Int) = messages[position].direction.ordinal

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dialogMessageEndOfGroupMark = itemView.dialogMessageEndOfGroupMark
        val dialogMessageText = itemView.dialogMessageText
        val dialogMessageSendTime = itemView.dialogMessageSendTime
        val dialogMessageContaienr = itemView.dialogMessageContainer
        val dialogMessageSendStatus = itemView.dialogMessageSendStatus
    }
}