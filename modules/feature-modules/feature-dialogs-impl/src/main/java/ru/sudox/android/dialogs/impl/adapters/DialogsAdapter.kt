package ru.sudox.android.dialogs.impl.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.sudox.android.core.ui.lists.ScreenListAdapter
import ru.sudox.android.dialogs.impl.R
import ru.sudox.android.dialogs.impl.holders.DialogFooterHolder
import ru.sudox.android.dialogs.impl.holders.DialogHolder
import ru.sudox.android.dialogs.impl.viewobjects.DialogViewObject
import ru.sudox.simplelists.BasicListHolder

const val DIALOG_ITEM_VIEW_TYPE = 0
const val DIALOG_FOOTER_VIEW_TYPE = 1

/**
 * Адаптер для списка диалогов
 *
 * @param onClicked Функция, которая будет вызвана при клике
 * @param fragment Связанный фрагмент.
 */
class DialogsAdapter(
    private val onClicked: (DialogViewObject) -> (Unit),
    private val fragment: Fragment
) : ScreenListAdapter() {

    override fun createItemViewHolder(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BasicListHolder<*> = if (viewType == DIALOG_ITEM_VIEW_TYPE) {
        DialogHolder(inflater.inflate(R.layout.item_dialog, parent, false), onClicked, fragment)
    } else {
        DialogFooterHolder(inflater.inflate(R.layout.item_dialogs_footer, parent, false))
    }
}