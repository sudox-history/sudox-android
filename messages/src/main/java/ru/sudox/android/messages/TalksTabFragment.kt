package ru.sudox.android.messages

import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import ru.sudox.android.core.controllers.ViewListController
import ru.sudox.android.core.controllers.tabs.TabsChildController
import ru.sudox.android.messages.adapters.DialogsAdapter
import ru.sudox.android.messages.vos.impl.TalkVO
import ru.sudox.design.viewlist.ViewList

class TalksTabFragment : ViewListController<DialogsAdapter>(), TabsChildController {

    private var adapter: DialogsAdapter? = null

    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        super.onChangeStarted(changeHandler, changeType)

        if (changeType.isEnter) {
            adapter!!.dialogsVOs.let {
                it.add(TalkVO(1L, true, false, System.currentTimeMillis(), 100500, false, false, true,
                        "Да ладно S63-й двиг! Я в отличии от тебя масло не жру!", 7L, "Двигателя BMW", "n74b66"))

                it.add(TalkVO(2L, false, false, System.currentTimeMillis() - 1000, 0, false, true, true,
                        "Да я тебе говорю, BMW тебе - не ТАЗ! Одной турбиной сыт не будешь!", 8L, "Сборище BMW'шников", "Максим"))

                it.add(TalkVO(3L, false, true, System.currentTimeMillis() - 10000, 0, true, false, false,
                        "Clarson, can you call to me?", 6L, "TopGear fans", "kerjen"))
            }
        }
    }

    override fun getAdapter(viewList: ViewList): DialogsAdapter? {
        adapter = DialogsAdapter(R.plurals.talks)
        return adapter
    }

    override fun canConfigureAppBarVO(): Boolean {
        return false
    }
}