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
        adapter.notifyDataSetChanged()
//        adapter.notifyItemMoved(fromPosition + 1, toPosition + 1)
    }

    override fun onChanged(position: Int, count: Int) {
        adapter.notifyDataSetChanged()
//        adapter.notifyItemRangeChanged(position + 1, count + 1)
    }

    override fun onInserted(position: Int, count: Int) {
        adapter.notifyDataSetChanged()
//        adapter.notifyItemRangeInserted(position + 1, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter.notifyDataSetChanged()
//        adapter.notifyItemRangeRemoved(position + 1, count)
    }

    override fun compare(oldItem: FriendVO, newItem: FriendVO): Int {
        return -oldItem.requestTime.compareTo(newItem.requestTime)
    }
}