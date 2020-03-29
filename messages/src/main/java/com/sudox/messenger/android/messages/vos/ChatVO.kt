package com.sudox.messenger.android.messages.vos

import android.content.Context
import android.view.View
import com.sudox.messenger.android.messages.R
import com.sudox.messenger.android.people.common.views.AvatarImageView
import com.sudox.messenger.android.people.common.vos.PeopleVO

data class ChatVO(
        override val dialogId: Int,
        override var isMuted: Boolean,
        override var isViewed: Boolean,
        override var time: Long,
        override var messagesCount: Int,
        override var isLastMessageByMe: Boolean,
        override var isSentMessageDelivered: Boolean,
        override var isSentMessageViewed: Boolean,
        override var isSentByUserMessage: Boolean,

        override var userId: Long,
        override var userName: String,
        override var seenTime: Long,
        override var photoId: Long,

        override var lastSentMessage: String) : DialogVO, PeopleVO {

    override fun getName(): String {
        return userName
    }

    override fun getLastMessage(context: Context): kotlin.String {
        if(isLastMessageByMe){
            return context.resources.getString(R.string.message_sent_by_user, lastSentMessage)
        }
        return lastSentMessage
    }

    override fun getAvatarView(context: Context): android.view.View {
        return AvatarImageView(context)
    }

    override fun bindAvatarView(view: android.view.View) {
        if(view is AvatarImageView){
            view.vo = this
        }
    }

    override fun unbindAvatarView(view: View) {
        if(view is AvatarImageView){
            view.vo = this
        }
    }

    override fun isAvatarViewTypeSame(view: android.view.View): kotlin.Boolean {
        return view is AvatarImageView
    }

    override fun getButtons(): kotlin.Array<kotlin.Triple<kotlin.Int, kotlin.Int, kotlin.Int>>? {
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