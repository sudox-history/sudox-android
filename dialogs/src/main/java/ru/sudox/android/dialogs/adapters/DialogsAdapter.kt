package ru.sudox.android.dialogs.adapters

import android.view.ViewGroup
import androidx.annotation.PluralsRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.dialogs.R
import ru.sudox.android.dialogs.callbacks.DialogsCallback
import ru.sudox.android.dialogs.views.DialogItemView
import ru.sudox.android.dialogs.vos.DialogVO
import ru.sudox.design.viewlist.ViewList
import ru.sudox.design.viewlist.ViewListAdapter

class DialogsAdapter(
        @PluralsRes val pluralId: Int,
        val glide: GlideRequests,
        val clickCallback: (Long) -> (Unit)
) : ViewListAdapter<DialogsAdapter.ViewHolder>() {

    val dialogsVOs = SortedList<DialogVO>(DialogVO::class.java, DialogsCallback(this))

    override var viewList: ViewList? = null
        set(value) {
            field = value?.apply {
                setItemViewCacheSize(20)
                setHasFixedSize(true)
            }
        }

    override fun createItemHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DialogItemView(parent.context)).apply {
            view.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    clickCallback(dialogsVOs[adapterPosition]!!.dialogId)
                }
            }
        }
    }

    override fun bindItemHolder(holder: ViewHolder, position: Int) {
        holder.view.setVO(dialogsVOs[position], glide)
    }

    override fun getFooterText(position: Int): String? {
        return if (position == itemCount - 1) {
            viewList!!.context.resources.getQuantityString(pluralId, dialogsVOs.size(), dialogsVOs.size())
        } else {
            null
        }
    }

    override fun getItemMargin(position: Int): Int {
        return viewList!!.context.resources.getDimensionPixelSize(R.dimen.dialogitemview_vertical_items_margin)
    }

    override fun getItemsCountAfterHeader(type: Int): Int {
        return dialogsVOs.size()
    }

    override fun getFooterCount(): Int {
        return 1
    }

    class ViewHolder(val view: DialogItemView) : RecyclerView.ViewHolder(view)
}