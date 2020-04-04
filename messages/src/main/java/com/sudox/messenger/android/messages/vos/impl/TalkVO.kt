package com.sudox.messenger.android.messages.vos.impl

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.color
import com.sudox.messenger.android.media.images.views.GlideCircleImageView
import com.sudox.messenger.android.messages.R
import com.sudox.messenger.android.messages.vos.DialogVO

data class TalkVO(
        override val dialogId: Long,
        override var isMuted: Boolean,
        override var isViewedByMe: Boolean,
        override var time: Long,
        override var messagesCount: Int,
        override var isSentMessageDelivered: Boolean,
        override var isSentMessageViewed: Boolean,
        override var isSentByUserMessage: Boolean,
        override var lastSentMessage: String,
        var talkImageId: Long,
        var talkName: String,
        var firstName: String?
) : DialogVO {

    override fun getName(): String {
        return talkName
    }

    override fun getLastMessage(context: Context): SpannableString {
        if (isSentByUserMessage) {
            val hintColor = ContextCompat.getColor(context, R.color.dialogitemview_message_sent_by_user_hint_color)
            val text = SpannableStringBuilder()
                    .color(hintColor) {
                        append(context.resources.getString(R.string.message_sent_by_different_user, firstName))
                    }
                    .append(lastSentMessage)

            return SpannableString.valueOf(text)
        }

        return SpannableString.valueOf(lastSentMessage)
    }

    override fun getAvatarView(context: Context): View {
        return GlideCircleImageView(context)
    }

    override fun bindAvatarView(view: View) {
        if (view is GlideCircleImageView) {
            view.loadImage(talkImageId)
        }
    }

    override fun unbindAvatarView(view: View) {
        if (view is GlideCircleImageView) {
            view.cancelLoading()
        }
    }

    override fun isAvatarViewTypeSame(view: View): Boolean {
        return view is GlideCircleImageView
    }
}