package ru.sudox.android.messages

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import ru.sudox.android.core.CoreController

const val MESSAGES_CONTROLLER_DIALOG_ID_KEY = "dialog_id"

class MessagesController : CoreController() {

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        return AppCompatTextView(activity!!).apply {
            text = args.getLong(MESSAGES_CONTROLLER_DIALOG_ID_KEY).toString()
        }
    }
}