package ru.sudox.android.people.common.vos

import android.content.Context
import ru.sudox.android.media.images.views.avatar.AvatarVO
import ru.sudox.android.people.common.R

const val SEEN_TIME_ONLINE = 0L
const val CLOSE_BUTTON_TAG = -1

val CLOSE_BUTTON = arrayOf(Triple(CLOSE_BUTTON_TAG, R.drawable.ic_cancel, R.color.verticalpeopleitemview_close_button_icon_tint))

/**
 * ViewObject человека.
 *
 * @property userId ID пользователя
 * @property userName Имя пользователя
 * @property photoId ID фотографии в хранилище.
 * @property seenTime Последнее время онлайна, (SEEN_TIME_ONLINE если пользователь онлайн в данный момент)
 */
interface PeopleVO : AvatarVO {

    var userId: Long
    var userName: String
    var seenTime: Long
    var photoId: Long

    override fun canShowIndicator(): Boolean {
        return !isStatusAboutOnline() && isUserOnline()
    }

    override fun getAvatarKey(): Long {
        return userId
    }

    override fun getTextInAvatar(): String? {
        return userName
    }

    override fun getResourceId(): Long {
        return photoId
    }

    /**
     * Возвращает триплеты тег-иконка-оттенок для функциональных кнопок
     *
     * @return Триплет тег-иконка-оттенок для функциональных кнопок.
     * Если равен null, то значит что кнопок нет
     */
    fun getButtons(): Array<Triple<Int, Int, Int>>?

    /**
     * Возвращает сообщение статуса
     *
     * @param context Контекст активности/приложения
     * @return Сообщение статуса
     */
    fun getStatusMessage(context: Context): String?

    /**
     * Определяет тип сообщения статуса.
     *
     * @return Сообщение статуса - отражение состояния данного пользователя (онлайн он или нет)?
     */
    fun isStatusAboutOnline(): Boolean

    /**
     * Определяет активность статуса.
     *
     * @return Активность статуса
     */
    fun isStatusActive(): Boolean

    /**
     * Определяет, онлайн ли данный пользователь
     *
     * @return Онлайн ли данный пользователь?
     */
    fun isUserOnline(): Boolean {
        return seenTime == SEEN_TIME_ONLINE
    }
}