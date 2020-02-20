package com.sudox.messenger.android.messages

import androidx.recyclerview.widget.SortedList
import com.sudox.messenger.android.messages.vos.DialogItemViewVO

class DialogsCallback(
        var adapter: DialogsAdapter
) : SortedList.Callback<DialogItemViewVO>() {
    override fun areItemsTheSame(item1: DialogItemViewVO?, item2: DialogItemViewVO?): Boolean {
        return item1 === item2
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
        adapter.notifyItemRangeRemoved(position, count)
    }

    override fun compare(item1: DialogItemViewVO, item2: DialogItemViewVO): Int {
        if (item1.isViewed && !item2.isViewed) {
            return 1
        }
        if (!item1.isViewed && item2.isViewed) {
            return -1
        }
        if (!item1.isViewed && !item2.isViewed) {
            if (item1.isMuted && !item2.isMuted) {
                return 1
            }
            if (!item1.isMuted && item2.isMuted) {
                return -1
            }
        }

        return -item1.date.compareTo(item2.date)
    }

    override fun areContentsTheSame(oldItem: DialogItemViewVO?, newItem: DialogItemViewVO?): Boolean {
        return oldItem == newItem
    }
}