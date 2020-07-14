package ru.sudox.android.people.impl.activity.viewobject

import ru.sudox.simplelists.model.BasicListViewObject

/**
 * ViewObject для истории стороннего пользователя
 *
 * @param publisherId ID публикатора
 * @param publisherName Имя публикатора
 * @param publisherAvatarId ID аватарки публикатора
 * @param isStoryViewed История просмотрена?
 * @param isStoryEvent История является событием?
 */
class OtherStoryViewObject(
    val publisherId: String,
    val publisherName: String,
    val publisherAvatarId: String,
    val isStoryViewed: Boolean,
    val isStoryEvent: Boolean
) : BasicListViewObject<String, OtherStoryViewObject> {
    override fun getId(): String = publisherId
}