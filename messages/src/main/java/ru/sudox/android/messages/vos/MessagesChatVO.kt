package ru.sudox.android.messages.vos

import android.content.Context
import ru.sudox.android.people.common.vos.SimplePeopleVO

class MessagesChatVO(
        override var userId: Long,
        override var userName: String,
        override var seenTime: Long,
        override var photoId: Long
) : SimplePeopleVO(userId, userName, photoId), MessagesTitleAppBarVO {

    override fun getTitle(context: Context): String {
        return userName
    }

    override fun getSubtitle(context: Context): String {
        return "Seen a last minute"
    }
}