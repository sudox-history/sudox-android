package ru.sudox.android.people.peopletab.vos

import android.content.Context
import ru.sudox.android.people.common.vos.PeopleVO

/**
 * ViewObject для добавленного друга.
 * Информацию по полям смотрите в классе PeopleVO
 */
data class AddedFriendVO(
        override var userId: Long,
        override var userName: String,
        override var seenTime: Long,
        override var photoId: Long,
        var status: String? = null,
        var importance: Int
) : PeopleVO {

    override fun getButtons(): Array<Triple<Int, Int, Int>>? {
        return null
    }

    override fun getStatusMessage(context: Context): String {
        return if (status == null) {
            super.getStatusMessage(context)!!
        } else {
            status!!
        }
    }

    override fun isStatusAboutOnline(): Boolean {
        return false
    }

    override fun isStatusActive(): Boolean {
        return isUserOnline() && status == null
    }
}