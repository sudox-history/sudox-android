package com.sudox.messenger.android.messages.vos.impl

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.core.text.toSpannable
import com.sudox.messenger.android.messages.R
import com.sudox.messenger.android.messages.vos.DialogVO
import com.sudox.messenger.android.people.common.views.AvatarImageView
import com.sudox.messenger.android.people.common.vos.PeopleVO

data class ChatVO(
        override val dialogId: Long,
        override var isMuted: Boolean,
        override var isViewedByMe: Boolean,
        override var time: Long,
        override var messagesCount: Int,
        override var isSentMessageDelivered: Boolean,
        override var isSentMessageViewed: Boolean,
        override var isSentByUserMessage: Boolean,
        override var userId: Long,
        override var userName: String,
        override var seenTime: Long,
        override var photoId: Long,
        override var lastSentMessage: String
) : DialogVO, PeopleVO {

    override fun getName(): String {
        return userName
    }

    override fun getLastMessage(context: Context): SpannableString {
        if (isSentByUserMessage) {
            val hintColor = ContextCompat.getColor(context, R.color.dialogitemview_message_sent_by_user_hint_color)
            val text = SpannableStringBuilder()
                    .color(hintColor) {
                        append(context.resources.getString(R.string.message_sent_by_user))
                        Log.d("APPEND", context.resources.getString(R.string.message_sent_by_user))
                    }
                    .append(lastSentMessage)

            return SpannableString.valueOf(text)
        }

        return SpannableString.valueOf(lastSentMessage)
    }

    override fun getAvatarView(context: Context): View {
        return AvatarImageView(context)
    }

    override fun bindAvatarView(view: View) {
        if (view is AvatarImageView) {
            view.vo = this
        }
    }

    override fun unbindAvatarView(view: View) {
        if (view is AvatarImageView) {
            view.vo = this
        }
    }

    override fun isAvatarViewTypeSame(view: View): Boolean {
        return view is AvatarImageView
    }

    override fun getButtons(): Array<Triple<Int, Int, Int>>? {
        return null
    }

    override fun getStatusMessage(context: Context): String? {
        return null
    }

    override fun isStatusAboutOnline(): Boolean {
        return false
    }

    override fun isStatusActive(): Boolean {
        return false
    }
}