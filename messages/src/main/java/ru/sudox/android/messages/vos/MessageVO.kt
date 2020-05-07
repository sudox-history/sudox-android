package ru.sudox.android.messages.vos

import android.content.Context
import ru.sudox.android.media.images.views.vos.AvatarVO
import ru.sudox.android.media.vos.MediaAttachmentVO
import ru.sudox.android.people.common.vos.PeopleVO

interface MessageVO {

    val id: String
    val text: String?
    val attachments: ArrayList<MediaAttachmentVO>?
    val likes: ArrayList<PeopleVO>?
    val sentByMe: Boolean
    val sentTime: Long

    /**
     * Возвращает статус сообщения.
     *
     * @param context Контекст приложения/активности
     * @return Статус сообщения (null если статус отсутствует)
     */
    fun getMessageStatus(context: Context): String? {
        return null
    }

    /**
     * ViewObject для аватарки отправителя сообщения
     *
     * @return ViewObject аватарки (null если отображать аватарку отправителя не нужно)
     */
    fun getSenderAvatarVO(): AvatarVO? {
        return null
    }
}