package ru.sudox.android.messages

import android.content.Context
import android.os.Bundle
import android.view.View
import ru.sudox.design.viewlist.ViewList
import ru.sudox.android.core.fragments.ViewListFragment
import ru.sudox.android.core.tabs.TabsChildFragment
import ru.sudox.android.messages.adapters.DialogsAdapter
import ru.sudox.android.messages.vos.impl.TalkVO

class TalksTabFragment : ViewListFragment<DialogsAdapter>(), TabsChildFragment {

    var dialogsAdapter = DialogsAdapter(R.plurals.talks)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogsAdapter.dialogsVOs.let {
            it.add(TalkVO(1L, true, false, System.currentTimeMillis(), 100500, false, false, true,
                    "Да ладно S63-й двиг! Я в отличии от тебя масло не жру!", 7L, "Двигателя BMW", "n74b66"))

            it.add(TalkVO(2L, false, false, System.currentTimeMillis() - 1000, 0, false, true, true,
                    "Да я тебе говорю, BMW тебе - не ТАЗ! Одной турбиной сыт не будешь!", 8L, "Сборище BMW'шников", "Максим"))

            it.add(TalkVO(3L, false, true, System.currentTimeMillis() - 10000, 0, true, false, false,
                    "Clarson, can you call to me?", 6L, "TopGear fans", "kerjen"))
        }
    }

    override fun getTitle(context: Context): String {
        return context.getString(R.string.talks)
    }

    override fun getAdapter(viewList: ViewList): DialogsAdapter? {
        return dialogsAdapter
    }

    override fun isAppBarConfiguredByRoot(): Boolean {
        return true
    }
}