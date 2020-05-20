package ru.sudox.android.news.vos

import android.content.Context
import ru.sudox.android.media.vos.MediaAttachmentVO
import ru.sudox.android.news.R
import ru.sudox.android.people.common.vos.SimplePeopleVO
import ru.sudox.android.time.formatters.FullTimeFormatter
import ru.sudox.android.time.timestampToString

const val MORE_OPTIONS_BUTTON_TAG = 1
const val IS_ACTION_DISABLED = -1

/**
 * ViewObject для новости и её автора.
 * Информацию по переменным, связанными с пользователем, смотрите в PeopleVO.
 *
 * @param isLikeSet Установлен ли лайк?
 * @param isDislikeSet Установлен ли дизлайк?
 * @param likesCount Количество лайков
 * @param dislikesCount Количество дизлайков
 * @param commentsCount Количество комментаривиев
 * @param sharesCount Количество репостов
 * @param attachments Список c вложениями.
 * @param publishTime Время публикации новости.
 * @param contentText Текст новости
 */
class NewsVO(
        override var userId: Long,
        override var userName: String,
        override var photoId: Long,
        var isLikeSet: Boolean,
        var isDislikeSet: Boolean,
        var likesCount: Int,
        var dislikesCount: Int,
        var commentsCount: Int,
        var sharesCount: Int,
        var attachments: ArrayList<MediaAttachmentVO>?,
        var publishTime: Long,
        var contentText: String?
) : SimplePeopleVO(userId, userName, photoId) {

    override fun getButtons(): Array<Triple<Int, Int, Int>>? {
        return arrayOf(Triple(MORE_OPTIONS_BUTTON_TAG, R.drawable.ic_more_vert, R.color.news_more_options_button_icon_tint))
    }

    override fun getStatusMessage(context: Context): String? {
        return timestampToString(context, formatter = FullTimeFormatter, timestamp = publishTime)
    }
}