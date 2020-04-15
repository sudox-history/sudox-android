package ru.sudox.android.messages

import android.view.View
import ru.sudox.android.core.controllers.ViewListController
import ru.sudox.android.messages.adapters.DialogsAdapter
import ru.sudox.android.messages.vos.impl.ChatVO
import ru.sudox.android.people.common.vos.SEEN_TIME_ONLINE
import ru.sudox.design.viewlist.ViewList

class ChatsTabController : ViewListController<DialogsAdapter>() {

    private var adapter: DialogsAdapter? = null

    override fun onAttach(view: View) {
        super.onAttach(view)

        adapter!!.dialogsVOs.let {
            it.add(ChatVO(1L, false, true, System.currentTimeMillis() - 30000, 0, false, false, true, 1L, "Максим Митюшкин", SEEN_TIME_ONLINE, 4L, "" +
                    "Мы тут M760Li до 900 сил чипанули. До сотки за 2,4 секунды разгоняется. Лютая дичь конечно получилась!"))

            it.add(ChatVO(2L, false, true, System.currentTimeMillis() - 10000, 0, false, true, true, 1L, "n74b66", 1L, 7L, "" +
                    "А - кулак на!"))

            it.add(ChatVO(3L, true, true, System.currentTimeMillis(), 0, true, true, false, 1L, "kerjen", 1L, 1L, "" +
                    "Замутил сам себя!"))

            it.add(ChatVO(4L, true, false, System.currentTimeMillis() - 10000, 1, false, false, true, 1L, "andy_", 1L, 5L, "" +
                    "Воу, теперь мое сообщение находится сверху!"))

            it.add(ChatVO(5L, false, false, System.currentTimeMillis() - 15000, 1, false, false, true, 1L, "isp", 1L, 3L, "" +
                    "Сударь, мое сообщение ещё выше и отображается в две линии!"))
        }
    }

    override fun getAdapter(viewList: ViewList): DialogsAdapter? {
        adapter = DialogsAdapter(R.plurals.chats)
        return adapter
    }

    override fun isChild(): Boolean {
        return true
    }
}