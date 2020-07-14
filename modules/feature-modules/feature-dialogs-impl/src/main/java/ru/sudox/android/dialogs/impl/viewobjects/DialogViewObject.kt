package ru.sudox.android.dialogs.impl.viewobjects

import ru.sudox.simplelists.model.BasicListViewObject

const val DIALOG_ONLINE_STATUS_CHANGED = 0

/**
 * ViewObject для отображения диалога.
 *
 * @param dialogId ID диалога
 * @param dialogName Название диалога
 * @param dialogAvatarId ID аватарки диалога
 * @param lastMessageSenderName Имя отправителя последнего сообщения
 * @param dialogLastMessage Последнее сообщение
 * @param dialogUnreadMessages Количество непрочитанных сообщений
 * @param isDialogMuted Заглушен ли диалог?
 * @param isUserOnline В сети ли пользователь?
 * @param isSentByMe Сообщение отправлено пользователем сессии?
 * @param dialogTime Время последнего изменения в диалоге.
 */
data class DialogViewObject(
    val dialogId: String,
    val dialogName: String,
    val dialogAvatarId: String?,
    val lastMessageSenderName: String?,
    val dialogLastMessage: String?,
    val dialogUnreadMessages: Int, // TODO:
    val isDialogMuted: Boolean,
    val isUserOnline: Boolean,
    val isSentByMe: Boolean,
    val dialogTime: Long
) : BasicListViewObject<String, DialogViewObject> {

    override fun getChangePayload(vo: DialogViewObject): List<Any>? = if (vo.isUserOnline != isUserOnline) {
        listOf(DIALOG_ONLINE_STATUS_CHANGED)
    } else {
        null
    }

    override fun getId(): String = dialogId
}