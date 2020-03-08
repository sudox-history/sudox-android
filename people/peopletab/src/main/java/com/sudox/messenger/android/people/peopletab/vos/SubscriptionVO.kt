package com.sudox.messenger.android.people.peopletab.vos

import android.content.Context
import com.sudox.messenger.android.people.common.vos.PeopleVO
import com.sudox.messenger.android.people.peopletab.R

/**
 * ViewObject для подписки.
 * Информацию по другим полям смотрите в классе PeopleVO
 *
 * @param status Статус пользователя
 * @param favorite Рейтинг по "любимости"
 * @param popular Рейтинг по популярности
 */
data class SubscriptionVO(
        override var userId: Long,
        override var userName: String,
        override var seenTime: Long,
        override var photoId: Long,
        var status: String?,
        var favorite: Int,
        var popular: Int
) : PeopleVO {

    override fun getButtons(): Array<Triple<Int, Int, Int>>? {
        return null
    }

    override fun getStatusMessage(context: Context): String {
        return status ?: context.getString(R.string.new_friend_request)
    }

    override fun isStatusAboutOnline(): Boolean {
        return false
    }

    override fun isStatusActive(): Boolean {
        return false
    }
}