package com.sudox.messenger.android.news.vos

import android.content.Context
import com.sudox.messenger.android.news.R
import com.sudox.messenger.android.people.common.vos.SimplePeopleVO
import com.sudox.messenger.android.time.formatTime

const val MORE_OPTIONS_BUTTON_TAG = 1

/**
 * ViewObject для новости и её автора.
 * Информацию по переменным, связанными с пользователем, смотрите в PeopleVO.
 *
 * @param contentText Текст новости
 * @param publishTime Время публикации новости.
 */
class NewsVO(
        override var userId: Long,
        override var userName: String,
        override var photoId: Long,
        var contentText: String?,
        var publishTime: Long
) : SimplePeopleVO(userId, userName, photoId) {

    override fun getButtons(): Array<Triple<Int, Int, Int>>? {
        return arrayOf(Triple(MORE_OPTIONS_BUTTON_TAG, R.drawable.ic_more_vert, R.color.news_more_options_button_icon_tint))
    }

    override fun getStatusMessage(context: Context): String? {
        return formatTime(context, fullFormat = true, time = publishTime)
    }
}