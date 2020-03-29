package com.sudox.messenger.android.messages.vos.impl

import android.content.Context
import android.view.View
import com.sudox.messenger.android.media.images.views.GlideCircleImageView
import com.sudox.messenger.android.messages.R
import com.sudox.messenger.android.messages.vos.DialogVO

data class TalkVO(
        override val dialogId: Long,
        override var isMuted: Boolean,
        override var isViewed: Boolean,
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

    override fun getLastMessage(context: Context): String {
        if (isSentByUserMessage) {
            return context.resources.getString(R.string.message_sent_by_different_user, firstName, lastSentMessage)
        }

        return context.resources.getString(R.string.message_sent_by_user, lastSentMessage)
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