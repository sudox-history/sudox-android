package ru.sudox.android.messages

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import ru.sudox.android.core.CoreController
import ru.sudox.android.media.vos.MediaAttachmentVO
import ru.sudox.android.media.vos.impls.ImageAttachmentVO
import ru.sudox.android.messages.views.MessageItemView
import ru.sudox.android.messages.vos.MessageVO
import ru.sudox.android.messages.vos.MessagesChatVO
import ru.sudox.android.messages.vos.appbar.BaseMessagesAppBarVO
import ru.sudox.android.people.common.vos.PeopleVO
import ru.sudox.android.people.common.vos.SimplePeopleVO

const val MESSAGES_CONTROLLER_DIALOG_ID_KEY = "dialog_id"

class MessagesChatController : CoreController() {

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        appBarVO = BaseMessagesAppBarVO<MessagesChatVO>(glide).apply {
            vo = MessagesChatVO(1L, "Maxim Mityushkin", 0L, 4L)
        }

        return MessageItemView(activity!!).apply {
            updatePadding(32, 20, 32, 20)

            setVO(object : MessageVO {
                override val id: String = ""
                override val text: String = "Тестовое сообщение"
                override val attachments: ArrayList<MediaAttachmentVO>? = null
                override val likes: ArrayList<PeopleVO> = arrayListOf(
                        SimplePeopleVO(1L, "Anton Yankin", 1L),
                        SimplePeopleVO(1L, "Anton Yankin", -1L),
                        SimplePeopleVO(1L, "Anton Yankin", 4L),
                        SimplePeopleVO(1L, "Anton Yankin", 8L)
                )
                override val isFirstMessage = false
                override val isSentByMe = false
                override val sentTime: Long = 0L

                override fun getMessageStatus(context: Context) = "Прочитано в 16:00"
            }, glide)
        }
    }
}