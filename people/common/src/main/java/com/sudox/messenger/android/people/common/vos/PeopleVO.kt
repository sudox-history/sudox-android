package com.sudox.messenger.android.people.common.vos

import android.content.Context

/**
 * ViewObject человека.
 *
 * @property userId ID пользователя
 * @property userName Имя пользователя
 * @property photoId ID фотографии в хранилище.
 * @property seenTime Последнее время онлайна, (SEEN_TIME_ONLINE если пользователь онлайн в данный момент)
 */
abstract class PeopleVO {

    var userId: Long = 0
    var userName: String? = null
    var seenTime: Long = 0
    var photoId: Long = 0

    /**
     * Возвращает пары тег-стиль для функциональных кнопок
     */
    abstract fun getButtons(): Array<Pair<Int, Int>>

    /**
     * Возвращает сообщение статуса
     *
     * @param context Контекст активности/приложения
     * @return Сообщение статуса
     */
    abstract fun getStatusMessage(context: Context): String

    /**
     * Определяет тип сообщения статуса.
     *
     * @return Сообщение статуса - отражение состояния данного пользователя (онлайн он или нет)?
     */
    abstract fun isStatusAboutOnline(): Boolean

    /**
     * Определяет активность статуса.
     *
     * @return Активность статуса
     */
    abstract fun isStatusActive(): Boolean

    /**
     * Определяет, онлайн ли данный пользователь
     *
     * @return Онлайн ли данный пользователь?
     */
    abstract fun isUserOnline(): Boolean
}