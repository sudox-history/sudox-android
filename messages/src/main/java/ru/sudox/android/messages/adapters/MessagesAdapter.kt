package ru.sudox.android.messages.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.messages.R
import ru.sudox.android.messages.callbacks.MessageSortCallback
import ru.sudox.android.messages.views.MessageItemView
import ru.sudox.android.messages.vos.MessageVO
import ru.sudox.design.viewlist.ViewListAdapter
import kotlin.math.max

class MessagesAdapter(
        private val glide: GlideRequests,
        private val context: Context
) : ViewListAdapter<MessagesAdapter.MessageViewHolder>() {

    val messageVOs = SortedList(MessageVO::class.java, MessageSortCallback(this))

    override fun createItemHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(MessageItemView(context))
    }

    override fun bindItemHolder(holder: MessageViewHolder, position: Int) {
        holder.view.setVO(messageVOs[position], glide)
    }

    override fun getFooterText(position: Int): String? {
        return if (position == messageVOs.size()) {
            context.getString(R.string.your_messages_are_protected_by_cloud_end_to_end_encryption)
        } else {
            null
        }
    }

    override fun getFooterCount(): Int {
        return 1
    }

    override fun getItemMargin(position: Int): Int {
        return if (position == max(messageVOs.size() - 1, 0)) {
            42 // Margin between footer and content
        } else if (position != messageVOs.size()) {
            val current = messageVOs[position]
            val prev = messageVOs[position + 1]

            if (current.senderId == prev.senderId) {
                12
            } else {
                24
            }
        } else {
            0
        }
    }

    override fun canCreateMarginViaDecorators(): Boolean {
        return true
    }

    override fun getItemsCountAfterHeader(type: Int): Int {
        return messageVOs.size()
    }

    class MessageViewHolder(val view: MessageItemView) : RecyclerView.ViewHolder(view)
}