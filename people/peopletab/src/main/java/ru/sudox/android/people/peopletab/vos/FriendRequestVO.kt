package ru.sudox.android.people.peopletab.vos

import android.content.Context
import ru.sudox.android.people.common.vos.PeopleVO
import ru.sudox.android.people.peopletab.R

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

    override fun getButtons(): Array<Triple<Int, Int, Int>> {
        return arrayOf(
                Triple(ACCEPT_REQUEST_BUTTON_TAG, R.drawable.ic_accept, R.color.accept_friend_request_button_icon_tint_color),
                Triple(REJECT_REQUEST_BUTTON_TAG, R.drawable.ic_cancel, R.color.reject_friend_request_button_icon_tint_color)
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