package ru.sudox.android.messages.adapters

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.messages.R
import ru.sudox.android.messages.views.MessageItemView
import ru.sudox.android.messages.vos.MessageVO
import ru.sudox.android.time.timestampToDateString
import ru.sudox.design.viewlist.ViewListAdapter

private const val DATE_ITEM_VIEW_TYPE = 1
private const val MESSAGE_ITEM_VIEW_TYPE = 0

class MessagesAdapter(
        private val glide: GlideRequests,
        private val context: Context
) : ViewListAdapter<RecyclerView.ViewHolder>() {

    private val messageVOs = ArrayList<MessageVO>()
    private val loadedPositionsWithDates = HashMap<String, Int>()
    private val loadedDatesAtPosition = HashMap<Int, String>()

    fun insertNewMessage(messageVO: MessageVO) {
        val dateString = timestampToDateString(context, timestamp = messageVO.sentTime)
        var position = itemCount

        if (!loadedPositionsWithDates.containsKey(dateString)) {
            loadedPositionsWithDates[dateString] = position
            loadedDatesAtPosition[position] = dateString
            notifyItemInserted(position)
            position++
        }

        messageVOs.add(messageVO)
        notifyItemInserted(position)
    }

    override fun getItemType(position: Int): Int {
        return if (loadedDatesAtPosition.containsKey(position)) {
            DATE_ITEM_VIEW_TYPE
        } else {
            MESSAGE_ITEM_VIEW_TYPE
        }
    }

    override fun createItemHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == DATE_ITEM_VIEW_TYPE) {
            DateViewHolder(AppCompatTextView(ContextThemeWrapper(context, R.style.Sudox_MessageTimeTextView)))
        } else {
            MessageViewHolder(MessageItemView(context))
        }
    }

    override fun bindItemHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DateViewHolder) {
            holder.view.text = loadedDatesAtPosition[position]
        } else if (holder is MessageViewHolder) {
            holder.view.setVO(messageVOs[position - loadedDatesAtPosition.count {
                it.key < position
            } - 1], glide)
        }
    }

    override fun getFooterText(position: Int): String? {
        return if (position == 0) {
            context.getString(R.string.your_messages_are_protected_by_cloud_end_to_end_encryption)
        } else {
            null
        }
    }

    override fun getFooterCount(): Int {
        return 1
    }

    override fun getItemMargin(position: Int): Int {
        return 12
    }

    override fun getItemsCountAfterHeader(type: Int): Int {
        return messageVOs.size + loadedDatesAtPosition.size
    }

    class MessageViewHolder(val view: MessageItemView) : RecyclerView.ViewHolder(view)
    class DateViewHolder(val view: AppCompatTextView) : RecyclerView.ViewHolder(view)
}