package ru.sudox.android.dialogs.impl.search.viewobjects

import ru.sudox.simplelists.model.BasicListViewObject

/**
 * ViewObject для найденного чата.
 *
 * @param userId ID пользователя
 * @param userName Имя пользователя
 * @param userSeenTime Время последнего захода пользователя в сеть
 * @param userAvatarId ID аватарки пользователя
 * @param isUserOnline В сети ли пользователь?
 */
data class FoundChatViewObject(
    val userId: String,
    val userName: String,
    val userSeenTime: Long,
    val userAvatarId: String?,
    val isUserOnline: Boolean
) : BasicListViewObject<String, FoundChatViewObject> {
    override fun getId(): String = userId
}