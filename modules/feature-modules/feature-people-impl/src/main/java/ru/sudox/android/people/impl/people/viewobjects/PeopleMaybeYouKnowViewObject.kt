package ru.sudox.android.people.impl.people.viewobjects

import ru.sudox.simplelists.model.BasicListViewObject

const val MAYBE_YOU_KNOW_ONLINE_STATUS_CHANGED = 0

/**
 * ViewObject возможно знакомого человека
 *
 * @param userId ID пользователя
 * @param name Имя пользователя
 * @param mutualFriends Количество знакомых друзей
 * @param description Описание пользователя
 * @param avatarId ID аватарки пользователя
 * @param isOnline Пользователь сейчас в сети?
 * @param isAd Это рекламная запись?
 */
data class PeopleMaybeYouKnowViewObject(
    val userId: String,
    val name: String,
    val mutualFriends: Int,
    val description: String? = null,
    val avatarId: String? = null,
    val isOnline: Boolean,
    val isAd: Boolean
) : BasicListViewObject<String, PeopleMaybeYouKnowViewObject> {

    override fun getChangePayload(vo: PeopleMaybeYouKnowViewObject): List<Any>? = if (vo.isOnline != isOnline) {
        listOf(MAYBE_YOU_KNOW_ONLINE_STATUS_CHANGED)
    } else {
        null
    }

    override fun getId(): String = userId
}