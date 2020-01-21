package com.sudox.messenger.android.messages.vos

import android.graphics.drawable.Drawable

data class DialogItemViewVO(
        val isUserOnline : Boolean,
        val isViewed: Boolean,
        val dialogPhoto: Drawable,
        val dialogName: String,
        val previewMessage: String,
        val dateReceived: String,
        val messagesCount: Int,
        val isLastMessageByMe: Boolean
)