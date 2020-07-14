package ru.sudox.android.people.impl.people.viewobjects

import ru.sudox.simplelists.model.BasicListViewObject

const val REQUEST_ONLINE_STATUS_CHANGED = 0

/**
 * ViewObject запроса подписки
 *
 * @param userId ID пользователя
 * @param name Имя пользователя
 * @param message Сообщение, приклепленное к запросу
 * @param avatarId ID аватарки
 * @param isOnline В сети ли сейчас пользователь?
 */
data class PeopleRequestViewObject(
    val userId: String,
    val name: String,
    val message: String?,
    val avatarId: String,
    val isOnline: Boolean
) : BasicListViewObject<String, PeopleRequestViewObject> {

    override fun getChangePayload(vo: PeopleRequestViewObject): List<Any>? = if (vo.isOnline != isOnline) {
        listOf(REQUEST_ONLINE_STATUS_CHANGED)
    } else {
        null
    }

    override fun getId(): String = userId
}