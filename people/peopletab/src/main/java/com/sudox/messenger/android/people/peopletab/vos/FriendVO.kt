package com.sudox.messenger.android.people.peopletab.vos

import android.graphics.drawable.Drawable

const val SEEN_TIME_ONLINE = 0L
const val IS_NOT_REQUEST_TIME = 0L

/**
 * ViewObject друга.
 *
 * @property id ID пользователя
 * @property name Имя пользователя
 * @property seenTime Последнее время онлайна, (SEEN_TIME_ONLINE если пользователь онлайн в данный момент)
 * @property requestTime Время отправки заявки (IS_NOT_REQUEST_TIME если это не заявка на добавление)
 * @property photo Аватарка пользователя
 */
data class FriendVO(
        val id: Long,
        val name: String,
        val seenTime: Long,
        val requestTime: Long,
        val photo: Drawable
)