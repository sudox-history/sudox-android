package ru.sudox.android.messages.vos

import android.content.Context
import ru.sudox.android.messages.vos.appbar.MessagesTitleAppBarVO
import ru.sudox.android.people.common.vos.PeopleVO

class MessagesChatVO(
        override var userId: Long,
        override var userName: String,
        override var seenTime: Long,
        override var photoId: Long
) : PeopleVO, MessagesTitleAppBarVO {

    override fun getTitle(context: Context): String {
        return userName
    }

    override fun getSubtitle(context: Context): String {
        return getStatusMessage(context)!!
    }

    override fun canShowIndicator(): Boolean {
        return false
    }

    override fun isSubtitleActive(): Boolean {
        return isUserOnline()
    }
}