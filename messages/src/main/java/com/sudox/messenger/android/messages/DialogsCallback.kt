package com.sudox.messenger.android.messages

import androidx.recyclerview.widget.SortedList
import com.sudox.messenger.android.messages.vos.DialogItemViewVO
import com.sudox.messenger.android.messages.vos.DialogVO

class DialogsCallback(
        var adapter: DialogsAdapter
) : SortedList.Callback<DialogVO>() {
    override fun areItemsTheSame(item1: DialogVO?, item2: DialogVO?): Boolean {
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

    override fun compare(item1: DialogVO, item2: DialogVO): Int {
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

        return -item1.time.compareTo(item2.time)
    }

    override fun areContentsTheSame(oldItem: DialogVO?, newItem: DialogVO?): Boolean {
        return oldItem == newItem
    }
}