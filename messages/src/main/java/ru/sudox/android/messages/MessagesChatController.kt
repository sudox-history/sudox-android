package ru.sudox.android.messages

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
            updatePadding(32, 32, 32, 32)

            Handler().postDelayed({
                setVO(object : MessageVO {
                    override val id: String = ""
                    override val text: String? = null
                    override val attachments: ArrayList<MediaAttachmentVO>? = arrayListOf(ImageAttachmentVO(7L).apply {
                        height = 387
                        width = 620
                    })
                    override val likes: ArrayList<PeopleVO> = arrayListOf(
                            SimplePeopleVO(1L, "Anton Yankin", 1L)
                    )
                    override val isFirstMessage = false
                    override val isSentByMe = false
                    override val sentTime: Long = 0L
                }, glide)
            }, 2000)

            Handler().postDelayed({
                setVO(object : MessageVO {
                    override val id: String = ""
                    override val text: String? = null
                    override val attachments: ArrayList<MediaAttachmentVO>? = arrayListOf(ImageAttachmentVO(7L).apply {
                        height = 387
                        width = 620
                    })
                    override val likes: ArrayList<PeopleVO> = arrayListOf(
                            SimplePeopleVO(1L, "Anton Yankin", 1L),
                            SimplePeopleVO(1L, "Anton Yankin", -1L)
                    )
                    override val isFirstMessage = false
                    override val isSentByMe = false
                    override val sentTime: Long = 0L
                }, glide)
            }, 4000)

            Handler().postDelayed({
                setVO(object : MessageVO {
                    override val id: String = ""
                    override val text: String? = null
                    override val attachments: ArrayList<MediaAttachmentVO>? = arrayListOf(ImageAttachmentVO(7L).apply {
                        height = 387
                        width = 620
                    })
                    override val likes: ArrayList<PeopleVO> = arrayListOf(
                            SimplePeopleVO(1L, "Anton Yankin", 1L),
                            SimplePeopleVO(1L, "Anton Yankin", -1L),
                            SimplePeopleVO(1L, "Anton Yankin", 4L)
                    )
                    override val isFirstMessage = false
                    override val isSentByMe = false
                    override val sentTime: Long = 0L
                }, glide)
            }, 6000)

            Handler().postDelayed({
                setVO(object : MessageVO {
                    override val id: String = ""
                    override val text: String? = null
                    override val attachments: ArrayList<MediaAttachmentVO>? = arrayListOf(ImageAttachmentVO(7L).apply {
                        height = 387
                        width = 620
                    })
                    override val likes: ArrayList<PeopleVO> = arrayListOf(
                            SimplePeopleVO(1L, "Anton Yankin", 1L),
                            SimplePeopleVO(1L, "Anton Yankin", -1L),
                            SimplePeopleVO(1L, "Anton Yankin", 4L),
                            SimplePeopleVO(1L, "Anton Yankin", 8L)
                    )
                    override val isFirstMessage = false
                    override val isSentByMe = false
                    override val sentTime: Long = 0L
                }, glide)
            }, 8000)

            Handler().postDelayed({
                setVO(object : MessageVO {
                    override val id: String = ""
                    override val text: String? = null
                    override val attachments: ArrayList<MediaAttachmentVO>? = arrayListOf(ImageAttachmentVO(7L).apply {
                        height = 387
                        width = 620
                    })
                    override val likes: ArrayList<PeopleVO> = arrayListOf(
                            SimplePeopleVO(1L, "Anton Yankin", 1L),
                            SimplePeopleVO(1L, "Anton Yankin", 4L),
                            SimplePeopleVO(1L, "Anton Yankin", 8L)
                    )
                    override val isFirstMessage = false
                    override val isSentByMe = false
                    override val sentTime: Long = 0L
                }, glide)
            }, 10000)

            Handler().postDelayed({
                setVO(object : MessageVO {
                    override val id: String = ""
                    override val text: String? = null
                    override val attachments: ArrayList<MediaAttachmentVO>? = arrayListOf(ImageAttachmentVO(7L).apply {
                        height = 387
                        width = 620
                    })
                    override val likes: ArrayList<PeopleVO> = arrayListOf(
                            SimplePeopleVO(1L, "Anton Yankin", 1L),
                            SimplePeopleVO(1L, "Anton Yankin", 8L)
                    )
                    override val isFirstMessage = false
                    override val isSentByMe = false
                    override val sentTime: Long = 0L
                }, glide)
            }, 12000)

            Handler().postDelayed({
                setVO(object : MessageVO {
                    override val id: String = ""
                    override val text: String? = null
                    override val attachments: ArrayList<MediaAttachmentVO>? = arrayListOf(ImageAttachmentVO(7L).apply {
                        height = 387
                        width = 620
                    })
                    override val likes: ArrayList<PeopleVO> = arrayListOf(
                            SimplePeopleVO(1L, "Anton Yankin", 1L)
                    )
                    override val isFirstMessage = false
                    override val isSentByMe = false
                    override val sentTime: Long = 0L
                }, glide)
            }, 14000)

            Handler().postDelayed({
                setVO(object : MessageVO {
                    override val id: String = ""
                    override val text: String? = null
                    override val attachments: ArrayList<MediaAttachmentVO>? = arrayListOf(ImageAttachmentVO(7L).apply {
                        height = 387
                        width = 620
                    })
                    override val likes: ArrayList<PeopleVO> = arrayListOf()
                    override val isFirstMessage = false
                    override val isSentByMe = false
                    override val sentTime: Long = 0L
                }, glide)
            }, 16000)
        }
    }
}