package com.sudox.messenger.android.friends.callbacks

import androidx.recyclerview.widget.SortedList
import com.sudox.messenger.android.friends.FriendsAdapter
import com.sudox.messenger.android.friends.vos.FriendVO

class FriendsCallback(
        val adapter: FriendsAdapter,
        val type: Int
) : SortedList.Callback<FriendVO>() {

    override fun areContentsTheSame(oldItem: FriendVO, newItem: FriendVO): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(item1: FriendVO, item2: FriendVO): Boolean {
        return item1 === item2
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMovedAfterHeader(type, fromPosition, toPosition)
    }

    override fun onChanged(position: Int, count: Int) {
        adapter.notifyItemRangeChangedAfterHeader(type, position, count)
    }

    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInsertedAfterHeader(type, position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter.notifyItemRangeRemovedAfterHeader(type, position, count)
    }

    override fun compare(oldItem: FriendVO, newItem: FriendVO): Int {
        return oldItem.name.compareTo(newItem.name)
    }
}