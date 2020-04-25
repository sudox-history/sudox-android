package ru.sudox.android.messages.vos.appbar

import android.content.Context
import android.view.View
import android.view.ViewGroup
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.media.images.views.avatar.AvatarImageView
import ru.sudox.android.messages.R
import ru.sudox.android.messages.views.MessagesTitleAppBarView
import ru.sudox.android.messages.vos.MessagesChatVO
import ru.sudox.design.appbar.vos.APPBAR_BACK_BUTTON_PARAMS
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.others.AppBarButtonParam
import ru.sudox.design.appbar.vos.others.NOT_USED_PARAMETER

class ChatMessagesAppBarVO(
        val glide: GlideRequests
) : AppBarVO {

    var messagesTitleAppBarView: MessagesTitleAppBarView? = null
    var avatarImageView: AvatarImageView? = null
    var vo: MessagesChatVO? = null

    override fun getButtonsAtLeft(): Array<AppBarButtonParam>? {
        return APPBAR_BACK_BUTTON_PARAMS
    }

    override fun getButtonsAtRight(): Array<AppBarButtonParam>? {
        return null
    }

    override fun getViewAtLeft(context: Context): View? {
        if (messagesTitleAppBarView == null) {
            messagesTitleAppBarView = MessagesTitleAppBarView(context)
        }

        return messagesTitleAppBarView!!.apply {
            vo = this@ChatMessagesAppBarVO.vo
        }
    }

    override fun getViewAtRight(context: Context): View? {
        if (avatarImageView == null) {
            avatarImageView = AvatarImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                        context.resources.getDimensionPixelSize(R.dimen.chatmessagesappbarvo_avatar_width),
                        context.resources.getDimensionPixelSize(R.dimen.chatmessagesappbarvo_avatar_height)
                )
            }
        }

        return avatarImageView!!.apply {
            setVO(this@ChatMessagesAppBarVO.vo, glide)
        }
    }

    override fun getTitle(): Int {
        return NOT_USED_PARAMETER
    }
}