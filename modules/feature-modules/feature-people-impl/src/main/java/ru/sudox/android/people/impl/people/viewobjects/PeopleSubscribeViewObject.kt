package ru.sudox.android.people.impl.people.viewobjects

import ru.sudox.simplelists.model.BasicListViewObject

/**
 * ViewObject для подписки
 *
 * @param userId ID пользователя
 * @param name Имя пользователя
 * @param status Статус пользователя
 * @param avatarId ID аватарки
 * @param onlineTimestamp Время последнего захода в сеть
 * @param isOnline В сети ли пользователь?
 */
class PeopleSubscribeViewObject(
    val userId: String,
    val name: String,
    val status: String? = null,
    val avatarId: String? = null,
    val onlineTimestamp: Long,
    val isOnline: Boolean
) : BasicListViewObject<String, PeopleSubscribeViewObject> {
    override fun getId(): String = userId
}