package ru.sudox.android.messages.vos

import android.content.Context
import android.text.SpannableString
import ru.sudox.android.people.common.vos.AvatarVO

interface DialogVO : AvatarVO {

    val dialogId: Long
    var isMuted: Boolean
    var isViewedByMe: Boolean
    var time: Long
    var messagesCount: Int
    var isSentMessageDelivered: Boolean
    var isSentMessageViewed: Boolean
    var isSentByUserMessage: Boolean
    var lastSentMessage: String

    /**
     * Возвращает имя диалога
     * @return имя диалога
     */
    fun getName(): String

    /**
     * Возвращает последнее сообщение в диалоге
     * @param context Контекст активности/приложения
     * @return последнее сообщение
     */
    fun getLastMessage(context: Context): SpannableString
}