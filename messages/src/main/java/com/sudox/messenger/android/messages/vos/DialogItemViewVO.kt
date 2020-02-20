package com.sudox.messenger.android.messages.vos

import android.graphics.drawable.Drawable
import com.sudox.messenger.android.messages.R
import java.util.*

val BROADCAST_ICON = R.style.Sudox_Messages_DialogItemView_BroadCastTitleIcon

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

    /**
     * Возвращает иконку заголовка диалога
     * @return Стиль для иконки заголовка.
     * Если равен null, то значит что иконки нет
     */
    fun getDialogTitleIcon():Int?
}