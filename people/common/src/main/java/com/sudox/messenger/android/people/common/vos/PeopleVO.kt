package com.sudox.messenger.android.people.common.vos

import android.content.Context

const val SEEN_TIME_ONLINE = 0L

/**
 * ViewObject человека.
 *
 * @property userId ID пользователя
 * @property userName Имя пользователя
 * @property photoId ID фотографии в хранилище.
 * @property seenTime Последнее время онлайна, (SEEN_TIME_ONLINE если пользователь онлайн в данный момент)
 */
interface PeopleVO {

    var userId: Long
    var userName: String
    var seenTime: Long
    var photoId: Long

    /**
     * Возвращает пары тег-стиль для функциональных кнопок
     *
     * @return Пары тег-стиль для функциональных кнопок.
     * Если равен null, то значит что кнопок нет
     */
    fun getButtons(): Array<Pair<Int, Int>>?

    /**
     * Возвращает сообщение статуса
     *
     * @param context Контекст активности/приложения
     * @return Сообщение статуса
     */
    fun getStatusMessage(context: Context): String

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