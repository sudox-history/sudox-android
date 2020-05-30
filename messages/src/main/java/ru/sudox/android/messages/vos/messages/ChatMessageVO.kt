package ru.sudox.android.messages.vos.messages

import ru.sudox.android.media.vos.MediaAttachmentVO
import ru.sudox.android.messages.vos.MessageVO
import ru.sudox.android.people.common.vos.PeopleVO

class ChatMessageVO(
        override val id: String,
        override val text: String?,
        override val senderId: String,
        override val attachments: ArrayList<MediaAttachmentVO>?,
        override val likes: ArrayList<PeopleVO>?,
        override val isSentByMe: Boolean,
        override val sentTime: Long
) : MessageVO {
    override var isFirstMessage: Boolean = true
}