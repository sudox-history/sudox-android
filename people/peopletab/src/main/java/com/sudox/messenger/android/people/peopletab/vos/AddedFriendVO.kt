package com.sudox.messenger.android.people.peopletab.vos

import android.content.Context
import com.sudox.messenger.android.people.common.vos.PeopleVO
import com.sudox.messenger.android.people.peopletab.R

/**
 * ViewObject для добавленного друга.
 * Информацию по полям смотрите в классе PeopleVO
 */
data class AddedFriendVO(
        override var userId: Long,
        override var userName: String,
        override var seenTime: Long,
        override var photoId: Long,
        var importance: Int = 0
) : PeopleVO {

    override fun getButtons(): Array<Pair<Int, Int>>? {
        return null
    }

    override fun getStatusMessage(context: Context): String {
        return if (isUserOnline()) {
            context.getString(R.string.online)
        } else {
            // TODO: replace to formatTime
            seenTime.toString()
        }
    }

    override fun isStatusAboutOnline(): Boolean {
        return true
    }

    override fun isStatusActive(): Boolean {
        return isUserOnline()
    }
}