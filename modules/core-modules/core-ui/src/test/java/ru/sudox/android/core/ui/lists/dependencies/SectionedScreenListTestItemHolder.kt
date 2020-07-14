package ru.sudox.android.core.ui.lists.dependencies

import android.view.View
import android.widget.TextView
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.model.BasicListItem

/**
 * ViewHolder для элемента тестовой секции
 *
 * @param view View, которая должна быть внутри ViewHolder
 */
class SectionedScreenListTestItemHolder(
    view: View
) : BasicListHolder<SectionedScreenListItemViewObject>(view) {

    override fun bind(item: BasicListItem<SectionedScreenListItemViewObject>, changePayload: List<Any>?) {
        (itemView as TextView).text = item.viewObject!!.title
    }
}