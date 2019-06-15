package com.sudox.android.ui.main.messages.dialogs

import androidx.lifecycle.MutableLiveData
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.helpers.formatDate
import com.sudox.android.data.models.messages.MessageDirection
import com.sudox.android.data.models.messages.MessageStatus
import com.sudox.android.data.models.messages.dialogs.Dialog
import com.sudox.design.helpers.formatHtml
import com.sudox.android.common.helpers.livedata.SingleLiveEvent
import kotlinx.android.synthetic.main.item_dialog.view.*

class DialogsAdapter(val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<DialogsAdapter.Holder>() {

    var dialogs: ArrayList<Dialog> = ArrayList()
    var clickedDialogLiveData: MutableLiveData<Dialog> = SingleLiveEvent()

    override fun getItemCount() = dialogs.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater
                .from(context)
                .inflate(R.layout.item_dialog, parent, false)

        val holder = Holder(view)

        // Save much RAM on long lists
        view.setOnClickListener {
            if (holder.adapterPosition != NO_POSITION)
                clickedDialogLiveData.postValue(dialogs[holder.adapterPosition])
        }

        return holder
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val dialog = dialogs[position]
        val recipient = dialog.recipient
        val lastMessage = dialog.lastMessage

        holder.dialogRecipientAvatar.bindUser(recipient)
        holder.dialogRecipientName.installText(recipient.name)
        holder.dialogLastMsgTime.installText(formatDate(context, lastMessage.date))
        holder.dialogLastMsgText.installText(if (lastMessage.direction == MessageDirection.TO) {
            formatHtml("<font color='#BDBDBD'>${context.resources.getString(R.string.you)}:</font> ${lastMessage.message}")
        } else {
            lastMessage.message
        })

        if (lastMessage.direction == MessageDirection.TO) {
            holder.dialogLastMsgTime.compoundDrawablePadding = (5 * context.resources.displayMetrics.density).toInt()
            holder.dialogLastMsgTime.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(when {
                lastMessage.status == MessageStatus.IN_DELIVERY -> R.drawable.ic_clock
                lastMessage.status == MessageStatus.DELIVERED -> R.drawable.ic_check
                else -> R.drawable.ic_error
            }), null, null, null)
        } else {
            holder.dialogLastMsgTime.compoundDrawablePadding = 0
            holder.dialogLastMsgTime.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

    class Holder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val dialogRecipientAvatar = view.dialogRecipientAvatar
        val dialogRecipientName = view.dialogRecipientName
        val dialogLastMsgTime = view.dialogLastMsgTime
        val dialogLastMsgText = view.dialogLastMsgText
    }
}