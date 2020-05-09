package ru.sudox.android.messages.callbacks

import ru.sudox.android.messages.vos.MessageVO
import ru.sudox.design.viewlist.ViewListAdapter
import ru.sudox.design.viewlist.ViewListCallback

class MessageSortCallback(
        viewListAdapter: ViewListAdapter<*>
) : ViewListCallback<MessageVO>(viewListAdapter) {

    override fun compare(first: MessageVO, second: MessageVO): Int {
        return -first.sentTime.compareTo(second.sentTime)
    }
}