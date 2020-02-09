package com.sudox.messenger.android.messages.vos

import android.graphics.drawable.Drawable
import java.util.*

data class DialogItemViewVO(
        val dialogId: Int,
        val isUserOnline : Boolean,
        val isMuted: Boolean,
        val isViewed: Boolean,
        val dialogPhoto: Drawable,
        val dialogName: String,
        val previewMessage: String,
        val dateView: String,
        val date: Long,
        val messagesCount: Int,
        val isLastMessageByMe: Boolean,
        val isSentMessageDelivered: Boolean,
        val isSentMessageViewed: Boolean
)