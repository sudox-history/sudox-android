package com.sudox.messenger.android.moments

import androidx.recyclerview.widget.SortedList
import com.sudox.messenger.android.moments.vos.MomentVO

class MomentsCallback(
        var adapter: MomentsAdapter
) : SortedList.Callback<MomentVO>() {

    override fun areContentsTheSame(oldItem: MomentVO, newItem: MomentVO): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(item1: MomentVO, item2: MomentVO): Boolean {
        return item1 === item2
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMoved(fromPosition + 1, toPosition + 1)
    }

    override fun onChanged(position: Int, count: Int) {
        adapter.notifyItemRangeChanged(position + 1, count)
    }

    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInserted(position + 1, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter.notifyItemRangeRemoved(position + 1, count)
    }

    override fun compare(oldItem: MomentVO, newItem: MomentVO): Int {
        if (oldItem.isFullyViewed && !newItem.isFullyViewed) {
            return 1
        } else if (!oldItem.isFullyViewed && newItem.isFullyViewed) {
            return -1
        }

        return -oldItem.publishTime.compareTo(newItem.publishTime)
    }
}