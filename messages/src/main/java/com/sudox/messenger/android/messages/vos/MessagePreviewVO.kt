package com.sudox.messenger.android.messages.vos

import android.graphics.drawable.Drawable

data class MessagePreviewVO(
        val isUserOnline : Boolean,
        val userPhoto: Drawable,
        val userName: String,
        val previewMessage: String,
        val dateReceived: String,
        val messagesCount: Int,
        val isLastMessageByMe: Boolean
)