package ru.sudox.android.messages

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import ru.sudox.android.core.CoreController
import ru.sudox.android.messages.vos.MessagesTalkVO
import ru.sudox.android.messages.vos.appbar.BaseMessagesAppBarVO

class MessagesTalkController : CoreController()  {

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        appBarVO = BaseMessagesAppBarVO<MessagesTalkVO>(glide).apply {
            vo = MessagesTalkVO(1L, "\"Inglourious Basterds\" aka the...", 15, 30, -1L)
        }

        return AppCompatTextView(activity!!).apply {
            text = args.getLong(MESSAGES_CONTROLLER_DIALOG_ID_KEY).toString()
        }
    }
}