package com.sudox.messenger.android.messages.vos

import android.graphics.drawable.Drawable

data class RoomMessagesDialogVO(
        override val dialogId: Int,
        override var isUserOnline: Boolean,
        override var isMuted: Boolean,
        override var isViewed: Boolean,
        override var dialogPhoto: Drawable,
        override var dialogName: String,
        override var previewMessage: String,
        override var dateView: String,
        override var date: Long,
        override var messagesCount: Int,
        override var isLastMessageByMe: Boolean,
        override var isSentMessageDelivered: Boolean,
        override var isSentMessageViewed: Boolean,
        var isIconEnabled:Boolean = false) : DialogItemViewVO {

    override fun getDialogTitleIcon(): Int? {
        return BROADCAST_ICON
    }
}