package ru.sudox.android.messages

import android.content.Context
import android.view.View
import ru.sudox.android.core.controllers.ViewListController
import ru.sudox.android.media.vos.MediaAttachmentVO
import ru.sudox.android.media.vos.impls.ImageAttachmentVO
import ru.sudox.android.messages.adapters.MessagesAdapter
import ru.sudox.android.messages.vos.MessageVO
import ru.sudox.android.messages.vos.MessagesChatVO
import ru.sudox.android.messages.vos.appbar.BaseMessagesAppBarVO
import ru.sudox.android.people.common.vos.PeopleVO
import ru.sudox.android.people.common.vos.SimplePeopleVO
import ru.sudox.design.viewlist.ViewList

const val MESSAGES_CONTROLLER_DIALOG_ID_KEY = "dialog_id"

class MessagesChatController : ViewListController<MessagesAdapter>(true) {

    override fun bindView(view: View) {
        super.bindView(view)

        appBarVO = BaseMessagesAppBarVO<MessagesChatVO>(glide).apply {
            vo = MessagesChatVO(1L, "Maxim Mityushkin", 0L, 4L)
        }

        adapter!!.messageVOs.add(object : MessageVO {
            override val id: String = ""
            override val text: String = "Первое сообщение в Sudox! Напиши если оно у тебя отображается"
            override val attachments: ArrayList<MediaAttachmentVO>? = null
            override val likes: ArrayList<PeopleVO>? = null
            override val isFirstMessage = false
            override val isSentByMe = false
            override val sentTime: Long = 0L

            override fun getMessageStatus(context: Context) = null
        })

        adapter!!.messageVOs.add(object : MessageVO {
            override val id: String = ""
            override val text: String = "Да, сообщение отображается."
            override val attachments: ArrayList<MediaAttachmentVO>? = null
            override val likes: ArrayList<PeopleVO>? = null
            override val isFirstMessage = false
            override val isSentByMe = true
            override val sentTime: Long = 1L

            override fun getMessageStatus(context: Context) = null
        })

        adapter!!.messageVOs.add(object : MessageVO {
            override val id: String = ""
            override val text: String = "Ну все, теперь проверяем отображение картинки"
            override val attachments: ArrayList<MediaAttachmentVO>? = null
            override val likes: ArrayList<PeopleVO>? = null
            override val isFirstMessage = false
            override val isSentByMe = false
            override val sentTime: Long = 2L

            override fun getMessageStatus(context: Context) = null
        })

        adapter!!.messageVOs.add(object : MessageVO {
            override val id: String = ""
            override val text: String? = null
            override val attachments: ArrayList<MediaAttachmentVO> = arrayListOf(ImageAttachmentVO(7L).apply {
                height = 387
                width = 620
            })
            override val likes: ArrayList<PeopleVO>? = null
            override val isFirstMessage = false
            override val isSentByMe = false
            override val sentTime: Long = 3L

            override fun getMessageStatus(context: Context) = null
        })

        adapter!!.messageVOs.add(object : MessageVO {
            override val id: String = "2"
            override val text: String? = null
            override val attachments: ArrayList<MediaAttachmentVO> = arrayListOf(ImageAttachmentVO(8L).apply {
                height = 1800
                width = 2880
            })
            override val likes: ArrayList<PeopleVO>? = null
            override val isFirstMessage = false
            override val isSentByMe = false
            override val sentTime: Long = 4L

            override fun getMessageStatus(context: Context) = null
        })
    }

    override fun getAdapter(viewList: ViewList): MessagesAdapter {
        return MessagesAdapter(glide, activity!!)
    }
}