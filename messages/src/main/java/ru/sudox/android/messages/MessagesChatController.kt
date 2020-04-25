package ru.sudox.android.messages

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import ru.sudox.android.core.CoreController
import ru.sudox.android.messages.vos.MessagesChatVO
import ru.sudox.android.messages.vos.appbar.BaseMessagesAppBarVO

const val MESSAGES_CONTROLLER_DIALOG_ID_KEY = "dialog_id"

class MessagesChatController : CoreController() {

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        appBarVO = BaseMessagesAppBarVO<MessagesChatVO>(glide).apply {
            vo = MessagesChatVO(1L, "Maxim Mityushkin", 0L, 4L)
        }

        return AppCompatTextView(activity!!).apply {
            text = args.getLong(MESSAGES_CONTROLLER_DIALOG_ID_KEY).toString()
        }
    }
}