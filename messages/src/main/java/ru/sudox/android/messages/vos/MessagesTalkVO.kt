package ru.sudox.android.messages.vos

import android.content.Context
import ru.sudox.android.media.images.views.vos.AvatarVO
import ru.sudox.android.messages.vos.appbar.MessagesTitleAppBarVO

class MessagesTalkVO(
        val talkId: Long,
        val talkName: String,
        val membersCount: Int,
        val unreadNewsCount: Int,
        val photoId: Long
) : MessagesTitleAppBarVO, AvatarVO {

    override fun getTitle(context: Context): String {
        return talkName
    }

    override fun getSubtitle(context: Context): String {
        return "10 members" // TODO: Format
    }

    override fun getAvatarKey(): Long {
        return talkId
    }

    override fun getTextInAvatar(): String? {
        return talkName
    }

    override fun isSubtitleActive(): Boolean {
        return false
    }

    override fun getNumberInIndicator(): Int {
        return unreadNewsCount
    }

    override fun canShowIndicator(): Boolean {
        return unreadNewsCount > 0
    }

    override fun getResourceId(): Long {
        return photoId
    }
}