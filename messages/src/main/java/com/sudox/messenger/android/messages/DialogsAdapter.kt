package com.sudox.messenger.android.messages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.messages.views.DialogItemView
import com.sudox.messenger.android.messages.vos.DialogItemViewVO
import com.sudox.messenger.android.messages.vos.DialogVO
import kotlinx.android.synthetic.main.dialogs_count.view.*
import java.util.*

class DialogsAdapter(val context: Context) : ViewListAdapter<DialogsAdapter.ViewHolder>() {

    val dialogs = SortedList<DialogVO>(DialogVO::class.java, DialogsCallback(this))
    var addDialogCallback: (() -> (Unit))? = null

    override var viewList: ViewList? = null
        set(value) {
            field = value?.apply {
                setItemViewCacheSize(20)
                setHasFixedSize(true)
            }
        }

    override fun getItemMargin(position: Int): Int {
        return viewList!!.context.resources.getDimensionPixelSize(R.dimen.dialogitemview_vertical_items_margin)
    }

    override fun createItemHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = DialogItemView(context)
        val holder = ViewHolder(view)
        view.setOnClickListener {
            if (holder.adapterPosition != -1) {
                dialogs[holder.adapterPosition].isMuted = !dialogs[holder.adapterPosition].isMuted
                notifyItemChanged(holder.adapterPosition)
            }
        }

        view.setOnLongClickListener {
            if (holder.adapterPosition != -1) {
                dialogs.removeItemAt(holder.adapterPosition)
                notifyItemChanged(holder.adapterPosition)
                notifyItemChanged(itemCount - 1)
            }
            true
        }

        return holder
    }

    override fun getItemsCountAfterHeader(type: Int): Int {
        return dialogs.size()
    }

    override fun bindItemHolder(holder: ViewHolder, position: Int) {
        holder.view.let {
            val dialog = dialogs[position]
            it as DialogItemView
            it.vo = dialog
        }
    }

    override fun getFooterText(position: Int): String? {
        return if (position == itemCount-1) {
            viewList!!.context.resources.getQuantityString(R.plurals.chats_count, dialogs.size(), dialogs.size())
        } else {
            null
        }
    }

    override fun getFooterCount(): Int {
        return 1
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}