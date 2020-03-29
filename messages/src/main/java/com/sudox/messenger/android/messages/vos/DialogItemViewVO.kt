package com.sudox.messenger.android.messages.vos

import com.sudox.messenger.android.media.images.vos.ImageVO

interface DialogItemViewVO : ImageVO {
    val dialogId: Int
    var isUserOnline: Boolean
    var isMuted: Boolean
    var isViewed: Boolean
    var dialogPhotoId: Long
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