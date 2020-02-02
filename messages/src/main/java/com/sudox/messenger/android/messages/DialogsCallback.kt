package com.sudox.messenger.android.messages

import androidx.recyclerview.widget.SortedList
import com.sudox.messenger.android.messages.vos.DialogItemViewVO

class DialogsCallback(
        var adapter: DialogsAdapter
) : SortedList.Callback<DialogItemViewVO>() {
    override fun areItemsTheSame(item1: DialogItemViewVO?, item2: DialogItemViewVO?): Boolean {
        return item1?.dialogId == item2?.dialogId
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMoved(fromPosition, toPosition)
    }

    override fun onChanged(position: Int, count: Int) {
        adapter.notifyItemRangeChanged(position, count)
    }

    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInserted(position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun compare(o1: DialogItemViewVO?, o2: DialogItemViewVO?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun areContentsTheSame(oldItem: DialogItemViewVO?, newItem: DialogItemViewVO?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}