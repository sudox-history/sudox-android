package com.sudox.android.ui.main.messages.dialogs

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.helpers.formatDate
import com.sudox.android.data.models.messages.MessageDirection
import com.sudox.android.data.models.messages.dialogs.Dialog
import com.sudox.design.helpers.formatHtml
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.android.synthetic.main.item_dialog.view.*

class DialogsAdapter(val context: Context) : RecyclerView.Adapter<DialogsAdapter.Holder>() {

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
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val dialogRecipientAvatar = view.dialogRecipientAvatar
        val dialogRecipientName = view.dialogRecipientName
        val dialogLastMsgTime = view.dialogLastMsgTime
        val dialogLastMsgText = view.dialogLastMsgText
    }
}