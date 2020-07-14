package ru.sudox.android.people.impl.activity.viewobject

import ru.sudox.simplelists.model.BasicListViewObject

/**
 * ViewObject для поста
 *
 * @param postId ID поста
 * @param postText Текст поста.
 * @param publisherId ID автора поста
 * @param publisherName Имя автора поста
 * @param publisherAvatarId ID аватарки автора поста
 * @param publishTime Время публикации.
 */
data class PostViewObject(
    val postId: String,
    val postText: String?,
    val publisherId: String,
    val publisherName: String,
    val publisherAvatarId: String,
    val publishTime: Long
) : BasicListViewObject<String, PostViewObject> {
    override fun getId(): String = postId
}