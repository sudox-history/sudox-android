package ru.sudox.android.people.impl.people.viewobjects

import ru.sudox.simplelists.model.BasicListViewObject

/**
 * ViewObject для подписки.
 *
 * @param userId ID пользователя
 * @param name Имя пользователя
 * @param avatarId ID аватарки пользователя
 * @param onlineTimestamp Время последнего захода в сеть
 * @param isOnline В сети ли пользователь?
 */
data class PeopleSubscriptionViewObject(
    val userId: String,
    val name: String,
    val avatarId: String? = null,
    val onlineTimestamp: Long,
    val isOnline: Boolean
) : BasicListViewObject<String, PeopleSubscriptionViewObject> {
    override fun getId(): String = userId
}