package com.sudox.messenger.android.messages.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.messages.R
import com.sudox.messenger.android.messages.callbacks.DialogsCallback
import com.sudox.messenger.android.messages.views.DialogItemView
import com.sudox.messenger.android.messages.vos.DialogVO

class DialogsAdapter : ViewListAdapter<DialogsAdapter.ViewHolder>() {

    val dialogsVOs = SortedList<DialogVO>(DialogVO::class.java, DialogsCallback(this))

    override var viewList: ViewList? = null
        set(value) {
            field = value?.apply {
                setItemViewCacheSize(20)
                setHasFixedSize(true)
            }
        }

    override fun createItemHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DialogItemView(parent.context))
    }

    override fun bindItemHolder(holder: ViewHolder, position: Int) {
        holder.view.vo = dialogsVOs[position]
    }

    override fun getFooterText(position: Int): String? {
        return if (position == itemCount - 1) {
            viewList!!.context.resources.getQuantityString(R.plurals.chats_count, dialogsVOs.size(), dialogsVOs.size())
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