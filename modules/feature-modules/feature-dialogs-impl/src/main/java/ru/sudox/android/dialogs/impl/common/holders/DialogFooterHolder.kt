package ru.sudox.android.dialogs.impl.common.holders

import android.view.View
import android.widget.TextView
import ru.sudox.android.dialogs.impl.common.viewobjects.DialogFooterViewObject
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.model.BasicListItem

/**
 * ViewHolder для футера списка диалогов.
 */
class DialogFooterHolder(view: View) : BasicListHolder<DialogFooterViewObject>(view) {

    override fun bind(item: BasicListItem<DialogFooterViewObject>, changePayload: List<Any>?) {
        (itemView as TextView).text = itemView.context.resources.getQuantityString(
            item.viewObject!!.pluralId,
            item.viewObject!!.count,
            item.viewObject!!.count
        )
    }
}