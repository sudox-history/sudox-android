package com.sudox.android.ui.main.messages.dialogs

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.data.database.model.ChatMessage
import com.sudox.android.data.database.model.User
import com.sudox.android.data.models.avatar.AvatarInfo
import com.sudox.android.data.models.avatar.impl.ColorAvatarInfo
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.design.helpers.drawAvatar
import com.sudox.design.helpers.drawCircleBitmap
import com.sudox.design.helpers.formatHtml
import com.sudox.design.helpers.getTwoFirstLetters
import kotlinx.android.synthetic.main.item_dialog.view.*
import java.util.*
import javax.inject.Inject

class DialogsAdapter @Inject constructor(val context: Context,
                                         val accountRepository: AccountRepository) : RecyclerView.Adapter<DialogsAdapter.Holder>() {

    var items: List<Pair<User, ChatMessage>> = arrayListOf()
    val accountId = accountRepository.cachedAccount?.id

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): Holder {
        return Holder(LayoutInflater
                .from(context)
                .inflate(R.layout.item_dialog, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindData(items[position])
    }

    inner class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val avatar = view.dialogRecipientAvatar!!
        val name = view.dialogRecipientName!!
        val lastMessage = view.dialogLastMessage!!
        val lastMessageDate = view.dialogLastMessageDate!!

        fun bindData(dialog: Pair<User, ChatMessage>) {
            val user = dialog.first
            val message = dialog.second

            bindAvatar(user)

            // Bind others data ...
            name.text = user.name
            lastMessageDate.text = DateFormat.format("HH:mm", Date(message.date)).toString()
            lastMessage.text = if (message.sender == accountId) {
                formatHtml("<font color='#FFFFFF'>${context.resources.getString(R.string.you)}:</font> ${message.message}")
            } else {
                message.message
            }
        }

        private fun bindAvatar(user: User) {
            val avatarInfo = AvatarInfo.parse(user.avatar)

            if (avatarInfo is ColorAvatarInfo) {
                drawCircleBitmap(view.context, drawAvatar(
                        text = user.name.getTwoFirstLetters(),
                        firstColor = avatarInfo.firstColor,
                        secondColor = avatarInfo.secondColor), avatar)
            }
        }
    }
}