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
import ru.sudox.android.people.common.vos.SimplePeopleVO

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

        createMessage(adapter, 1)
        createMessage(adapter, 2)
        createMessage(adapter, 3)
        createMessage(adapter, 4)
        createMessage(adapter, 5)
        createMessage(adapter, 6)
        createMessage(adapter, 7)
        createMessage(adapter, 8)
        createMessage(adapter, 9)
        createMessage(adapter, 10)

        createTextMessage(adapter, "Maecenas laoreet neque vitae ante malesuada tempus. Ut faucibus" +
                " placerat arcu eget pellentesque. Nam eget consequat odio. Suspendisse ull" +
                "amcorper leo ac odio consectetur, quis consectetur turpis tristique. S" +
                "uspendisse potenti. Nam quis tellus eros. Integer sit amet consectetur mi.", true)

        createTextMessage(adapter, "Maecenas", true)

        createTextMessage(adapter, "Maecenas laoreet neque vitae ante malesuada tempus. Ut faucibus" +
                " placerat arcu eget pellentesque. Nam eget consequat odio. Suspendisse ull" +
                "amcorper leo ac odio consectetur, quis consectetur turpis tristique. S" +
                "uspendisse potenti. Nam quis tellus eros. Integer sit amet consectetur mi.", false)

        createTextMessage(adapter, "Maecenas laoreet neque vitae ante malesuada tempus. Ut faucibus" +
                " placerat arcu eget pellentesque. Nam eget consequat odio. Suspendisse ull" +
                "amcorper leo ac odio consectetur, quis consectetur turpis tristique. S" +
                "uspendisse potenti. Nam quis tellus eros. Integer sit amet consect ", false)

        createTextMessage(adapter, "Тест\nТест", true)
    }

    private fun createTextMessage(adapter: MessagesAdapter, text: String, first: Boolean) {
        val likes = ArrayList<PeopleVO>()

        repeat(80) {
            likes.add(SimplePeopleVO(1L, "TheMax", 1L))
            likes.add(SimplePeopleVO(2L, "TheMax", 2L))
            likes.add(SimplePeopleVO(3L, "TheMax", 3L))
        }

        adapter.insertNewMessage(object : MessageVO {
            override val id: String = ""
            override val senderId: String = "1"
            override val text: String = text
            override val attachments: ArrayList<MediaAttachmentVO>? = null
            override val likes: ArrayList<PeopleVO>? = likes
            override val isFirstMessage = first
            override val isSentByMe = false
            override val sentTime: Long = 3L

            override fun getMessageStatus(context: Context) = null
        })

        adapter.insertNewMessage(object : MessageVO {
            override val id: String = ""
            override val senderId: String = "1"
            override val text: String = text
            override val attachments: ArrayList<MediaAttachmentVO>? = null
            override val likes: ArrayList<PeopleVO>? = likes
            override val isFirstMessage = first
            override val isSentByMe = true
            override val sentTime: Long = 3L

            override fun getMessageStatus(context: Context) = null
        })
    }

    private fun createMessage(adapter: MessagesAdapter, count: Int) {
        val attachments = ArrayList<MediaAttachmentVO>()
        val likes = ArrayList<PeopleVO>()

        repeat(count) {
            attachments.add(ImageAttachmentVO(8L).apply {
                height = 1800
                width = 2880
            })
        }

        repeat(80) {
            likes.add(SimplePeopleVO(1L, "TheMax", 1L))
            likes.add(SimplePeopleVO(2L, "TheMax", 2L))
            likes.add(SimplePeopleVO(3L, "TheMax", 3L))
        }

        adapter.insertNewMessage(object : MessageVO {
            override val id: String = ""
            override val senderId: String = "1"
            override val text: String? = null
            override val attachments: ArrayList<MediaAttachmentVO> = attachments
            override val likes: ArrayList<PeopleVO>? = likes
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
            override val likes: ArrayList<PeopleVO>? = likes
            override val isFirstMessage = false
            override val isSentByMe = true
            override val sentTime: Long = 5L

            override fun getMessageStatus(context: Context) = "Прочитано в 12:00"
        })
    }
}