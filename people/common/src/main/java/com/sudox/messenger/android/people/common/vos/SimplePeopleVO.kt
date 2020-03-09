package com.sudox.messenger.android.people.common.vos

import android.content.Context

/**
 * Предельно простая реализация PeopleVO.
 *
 * Можно использовать для связи с другими в VO в тех случаях, когда
 * PeopleItemView не используется.
 */
class SimplePeopleVO(
        override var userId: Long,
        override var userName: String,
        override var photoId: Long
) : PeopleVO {

    override var seenTime: Long = 0

    override fun getButtons(): Array<Triple<Int, Int, Int>>? {
        return null
    }

    override fun getStatusMessage(context: Context): String? {
        return null
    }

    override fun isStatusAboutOnline(): Boolean {
        // Дабы не отображать индикатор онлайна
        return true
    }

    override fun isStatusActive(): Boolean {
        return false
    }
}