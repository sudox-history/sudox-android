package ru.sudox.android.dialogs

import android.view.View
import ru.sudox.android.core.controllers.ViewListController
import ru.sudox.android.dialogs.adapters.DialogsAdapter
import ru.sudox.android.dialogs.vos.impl.TalkVO
import ru.sudox.android.messages.MESSAGES_CONTROLLER_DIALOG_ID_KEY
import ru.sudox.android.messages.MessagesChatController
import ru.sudox.android.messages.MessagesTalkController
import ru.sudox.design.viewlist.ViewList

class TalksTabFragment : ViewListController<DialogsAdapter>() {

    override fun bindView(view: View) {
        super.bindView(view)

        adapter!!.dialogsVOs.let {
            it.add(TalkVO(1L, true, false, System.currentTimeMillis(), 100500, false, false, true,
                    "Да ладно S63-й двиг! Я в отличии от тебя масло не жру!", 7L, "Двигателя BMW", "n74b66"))

            it.add(TalkVO(2L, false, false, System.currentTimeMillis() - 1000, 0, false, true, true,
                    "Да я тебе говорю, BMW тебе - не ТАЗ! Одной турбиной сыт не будешь!", 8L, "Сборище BMW'шников", "Максим"))

            it.add(TalkVO(3L, false, true, System.currentTimeMillis() - 10000, 0, true, false, false,
                    "Clarson, can you call to me?", 6L, "TopGear fans", "kerjen"))
        }
    }

    override fun getAdapter(viewList: ViewList): DialogsAdapter? {
        return DialogsAdapter(R.plurals.talks, glide) {
            navigationManager!!.showSubRoot(MessagesTalkController().apply {
                args.putLong(MESSAGES_CONTROLLER_DIALOG_ID_KEY, it)
            })
        }
    }

    override fun isChild(): Boolean {
        return true
    }
}