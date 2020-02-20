package com.sudox.messenger.android.people.peopletab.vos

import android.content.Context
import com.sudox.messenger.android.people.common.vos.PeopleVO
import com.sudox.messenger.android.people.peopletab.R

const val ACCEPT_REQUEST_BUTTON_TAG = 1
const val REJECT_REQUEST_BUTTON_TAG = 2

/**
 * ViewObject для заявки в друзья.
 * Информацию по другим полям смотрите в классе PeopleVO
 *
 * @param requestMessage Текст заявки
 * @param requestTime Время подачи заявки
 */
data class FriendRequestVO(
        override var userId: Long,
        override var userName: String,
        override var seenTime: Long,
        override var photoId: Long,
        var requestMessage: String?,
        var requestTime: Long
) : PeopleVO {

    override fun getButtons(): Array<Pair<Int, Int>> {
        return arrayOf(
                Pair(R.style.Sudox_People_Buttons_AcceptFriendRequest, ACCEPT_REQUEST_BUTTON_TAG),
                Pair(R.style.Sudox_People_Buttons_RejectFriendRequest, REJECT_REQUEST_BUTTON_TAG)
        )
    }

    override fun getStatusMessage(context: Context): String {
        return requestMessage ?: context.getString(R.string.new_friend_request)
    }

    override fun isStatusAboutOnline(): Boolean {
        return false
    }

    override fun isStatusActive(): Boolean {
        return false
    }
}