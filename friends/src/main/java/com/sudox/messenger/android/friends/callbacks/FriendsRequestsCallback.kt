package com.sudox.messenger.android.friends.callbacks

import androidx.recyclerview.widget.SortedList
import com.sudox.messenger.android.friends.FriendsAdapter
import com.sudox.messenger.android.friends.vos.FriendVO

class FriendsRequestsCallback(
        val adapter: FriendsAdapter
) : SortedList.Callback<FriendVO>() {

    override fun areContentsTheSame(oldItem: FriendVO, newItem: FriendVO): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(item1: FriendVO, item2: FriendVO): Boolean {
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

    override fun compare(oldItem: FriendVO, newItem: FriendVO): Int {
        return -oldItem.requestTime.compareTo(newItem.requestTime)
    }
}