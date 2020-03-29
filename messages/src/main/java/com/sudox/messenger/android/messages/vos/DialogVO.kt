package com.sudox.messenger.android.messages.vos

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import com.sudox.messenger.android.people.common.views.AvatarImageView

interface DialogVO {

    val dialogId: Long
    var isMuted: Boolean
    var isViewed: Boolean
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
    fun getLastMessage(context: Context): String

    /**
     * Возвращает View аватара диалога
     * @param context Контекст активности/приложения
     * @return View аватара диалога
     */
    fun getAvatarView(context: Context): View

    /**
     * Привязать view к аватару
     * @param view для привязки
     */
    fun bindAvatarView(view: View)

    /**
     * Отвязать view от аватара
     * @param view от которого необходимо отвязать
     */
    fun unbindAvatarView(view: View)

    /**
     * Определяет, одинаковые ли типы у аватара диалога и переданного view
     * @param view, с которой необходимо сравнить текущий AvatarView
     */
    fun isAvatarViewTypeSame(view: View): Boolean
}