package com.sudox.messenger.android.people.peopletab.vos

import android.graphics.drawable.Drawable

/**
 * ViewObject возможного друга.
 *
 * @property id ID пользователя
 * @property name Имя пользователя
 * @property isOnline Онлайн ли этот пользователь?
 * @property mutualFriendsCount Количество возможных друзей
 * @property photo Аватарка пользователя
 */
data class MaybeYouKnowVO(
        val id: Long,
        val name: String,
        val isOnline: Boolean,
        val mutualFriendsCount: Int,
        val photo: Drawable
)