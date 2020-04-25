package ru.sudox.android.dialogs

import android.view.View
import ru.sudox.android.core.controllers.ViewListController
import ru.sudox.android.dialogs.adapters.DialogsAdapter
import ru.sudox.android.dialogs.vos.impl.ChatVO
import ru.sudox.android.media.images.views.NOT_SHOWING_IMAGE_ID
import ru.sudox.android.messages.MESSAGES_CONTROLLER_DIALOG_ID_KEY
import ru.sudox.android.messages.MessagesChatController
import ru.sudox.android.people.common.vos.SEEN_TIME_ONLINE
import ru.sudox.design.viewlist.ViewList

class ChatsTabController : ViewListController<DialogsAdapter>() {

    override fun bindView(view: View) {
        super.bindView(view)

        adapter!!.dialogsVOs.let {
            it.add(ChatVO(1L, false, true, System.currentTimeMillis() - 30000, 0, false, false, true, 1L, "Максим Митюшкин", SEEN_TIME_ONLINE, NOT_SHOWING_IMAGE_ID, "" +
                    "Мы тут M760Li до 900 сил чипанули. До сотки за 2,4 секунды разгоняется. Лютая дичь конечно получилась!"))

            it.add(ChatVO(2L, false, true, System.currentTimeMillis() - 10000, 0, false, true, true, 1L, "n74b66", 1L, 7L, "" +
                    "А - кулак на!"))

            it.add(ChatVO(3L, true, true, System.currentTimeMillis(), 0, true, true, false, 1L, "kerjen", 1L, 1L, "" +
                    "Замутил сам себя!"))

            it.add(ChatVO(4L, true, false, System.currentTimeMillis() - 10000, 1, false, false, true, 1L, "andy_", 1L, 5L, "" +
                    "Воу, теперь мое сообщение находится сверху!"))

            it.add(ChatVO(5L, false, false, System.currentTimeMillis() - 15000, 1, false, false, true, 1L, "isp", 1L, 3L, "" +
                    "Сударь, мое сообщение ещё выше и отображается в две линии!"))

            var counter = 1L

            adapter!!.dialogsVOs.beginBatchedUpdates()

            repeat(300) {
                adapter!!.dialogsVOs.add(ChatVO(1L, false, true, counter++, 0, false, false, true, counter, "$counter", SEEN_TIME_ONLINE, NOT_SHOWING_IMAGE_ID, "" +
                        "Мы тут M760Li до 900 сил чипанули. До сотки за 2,4 секунды разгоняется. Лютая дичь конечно получилась!"))
            }

            adapter!!.dialogsVOs.endBatchedUpdates()
        }
    }

    override fun getAdapter(viewList: ViewList): DialogsAdapter? {
        return DialogsAdapter(R.plurals.chats, glide) {
            navigationManager!!.showSubRoot(MessagesChatController().apply {
                args.putLong(MESSAGES_CONTROLLER_DIALOG_ID_KEY, it)
            })
        }
    }

    override fun isChild(): Boolean {
        return true
    }
}