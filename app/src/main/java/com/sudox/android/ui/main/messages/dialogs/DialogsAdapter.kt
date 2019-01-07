package com.sudox.android.ui.main.messages.dialogs

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.data.models.avatar.AvatarInfo
import com.sudox.android.data.models.avatar.impl.ColorAvatarInfo
import com.sudox.android.data.models.messages.MessageDirection
import com.sudox.android.data.models.messages.dialogs.Dialog
import com.sudox.design.helpers.drawAvatar
import com.sudox.design.helpers.drawCircleBitmap
import com.sudox.design.helpers.formatHtml
import com.sudox.design.helpers.getTwoFirstLetters
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.android.synthetic.main.item_dialog.view.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class DialogsAdapter @Inject constructor(val context: Context) : RecyclerView.Adapter<DialogsAdapter.Holder>() {

    internal var dialogs: ArrayList<Dialog> = ArrayList()
    internal var clickedDialogLiveData: MutableLiveData<Dialog> = SingleLiveEvent()

    override fun getItemCount() = dialogs.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(context).inflate(R.layout.item_dialog, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindData(dialogs[position])
    }

    inner class Holder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bindData(dialog: Dialog) {
            bindAvatar(dialog)

            // Bind data ...
            view.dialogRecipientName.text = dialog.user.name
            view.dialogLastMessageDate.text = DateFormat.format("HH:mm", Date(dialog.message.date)).toString()
            view.dialogLastMessage.text = if (dialog.message.direction == MessageDirection.TO) {
                formatHtml("<font color='#FFFFFF'>${context.resources.getString(R.string.you)}:</font> ${dialog.message.message}")
            } else {
                dialog.message.message
            }

            // Bind click listener
            view.setOnClickListener { clickedDialogLiveData.postValue(dialog) }
        }

        private fun bindAvatar(dialog: Dialog) {
            val avatarInfo = AvatarInfo.parse(dialog.user.photo)

            if (avatarInfo is ColorAvatarInfo) {
                drawCircleBitmap(view.context, drawAvatar(
                        text = dialog.user.name.getTwoFirstLetters(),
                        firstColor = avatarInfo.firstColor,
                        secondColor = avatarInfo.secondColor), view.dialogRecipientAvatar)
            }
        }
    }
}