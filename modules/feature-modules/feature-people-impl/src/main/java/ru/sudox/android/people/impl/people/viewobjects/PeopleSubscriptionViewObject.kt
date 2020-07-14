package ru.sudox.android.people.impl.people.viewobjects

import ru.sudox.simplelists.model.BasicListViewObject

const val SUBSCRIPTION_ONLINE_STATUS_CHANGED = 0

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

    override fun getChangePayload(vo: PeopleSubscriptionViewObject): List<Any>? = if (vo.isOnline != isOnline) {
        listOf(SUBSCRIPTION_ONLINE_STATUS_CHANGED)
    } else {
        null
    }

    override fun getId(): String = userId
}