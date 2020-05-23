package ru.sudox.android.messages

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import ru.sudox.android.core.CoreController
import ru.sudox.android.media.vos.MediaAttachmentVO
import ru.sudox.android.media.vos.impls.ImageAttachmentVO
import ru.sudox.android.messages.adapters.MessagesAdapter
import ru.sudox.android.messages.views.MessageScreenLayout
import ru.sudox.android.messages.vos.MessageVO
import ru.sudox.android.messages.vos.MessagesChatVO
import ru.sudox.android.messages.vos.appbar.BaseMessagesAppBarVO
import ru.sudox.android.people.common.vos.PeopleVO

const val MESSAGES_CONTROLLER_DIALOG_ID_KEY = "dialog_id"

class MessagesChatController : CoreController() {

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        return MessageScreenLayout(activity!!)
    }

    override fun bindView(view: View) {
        super.bindView(view)

        appBarVO = BaseMessagesAppBarVO<MessagesChatVO>(glide).apply {
            vo = MessagesChatVO(1L, "Maxim Mityushkin", 0L, 4L)
        }

        val adapter = MessagesAdapter(glide, activity!!)

        if (view is MessageScreenLayout) {
            view.setAdapter(adapter)
        }

//        val attachments: ArrayList<MediaAttachmentVO> = arrayListOf(ImageAttachmentVO(8L).apply {
//            height = 1800
//            width = 2880
//        }, ImageAttachmentVO(8L).apply {
//            height = 1800
//            width = 2880
//        }, ImageAttachmentVO(8L).apply {
//            height = 1800
//            width = 2880
//        })

        val attachments = ArrayList<MediaAttachmentVO>()

        repeat(10) {
            attachments.add(ImageAttachmentVO(8L).apply {
                height = 1800
                width = 2880
            })
        }

        adapter.insertNewMessage(object : MessageVO {
            override val id: String = ""
            override val senderId: String = "1"
            override val text: String? = null
            override val attachments: ArrayList<MediaAttachmentVO> = attachments
            override val likes: ArrayList<PeopleVO>? = null
            override val isFirstMessage = false
            override val isSentByMe = false
            override val sentTime: Long = 3L

            override fun getMessageStatus(context: Context) = null
        })

        adapter.insertNewMessage(object : MessageVO {
            override val id: String = ""
            override val senderId: String = "1"
            override val text: String? = null
            override val attachments: ArrayList<MediaAttachmentVO> = attachments
            override val likes: ArrayList<PeopleVO>? = null
            override val isFirstMessage = false
            override val isSentByMe = true
            override val sentTime: Long = 5L

            override fun getMessageStatus(context: Context) = null
        })
    }
}