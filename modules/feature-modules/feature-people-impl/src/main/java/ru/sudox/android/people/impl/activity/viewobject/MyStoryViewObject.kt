package ru.sudox.android.people.impl.activity.viewobject

import ru.sudox.simplelists.model.BasicListViewObject

/**
 * ViewObject для истории, опубликанной пользователей
 *
 * @param publisherId ID публикатора
 * @param publisherName Имя публикатора
 * @param publisherAvatarId ID аватарки публикатора
 * @param isPublished Опубликана ли история
 */
class MyStoryViewObject(
    val publisherId: String,
    val publisherName: String,
    val publisherAvatarId: String?,
    val isPublished: Boolean
) : BasicListViewObject<String, MyStoryViewObject> {
    override fun getId(): String = publisherId
}