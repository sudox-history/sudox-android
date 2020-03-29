package com.sudox.messenger.android.messages.vos

import android.graphics.drawable.Drawable

interface DialogItemViewVO {
    val dialogId: Int
    var isUserOnline: Boolean
    var isMuted: Boolean
    var isViewed: Boolean
    var dialogPhoto: Drawable
    var dialogName: String
    var previewMessage: String
    var dateView: String
    var date: Long
    var messagesCount: Int
    var isLastMessageByMe: Boolean
    var isSentMessageDelivered: Boolean
    var isSentMessageViewed: Boolean
    var lastMessageUsername: String?
}