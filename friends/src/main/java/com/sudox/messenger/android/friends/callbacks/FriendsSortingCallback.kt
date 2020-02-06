package com.sudox.messenger.android.friends.callbacks

import com.sudox.design.viewlist.ViewListCallback
import com.sudox.messenger.android.friends.adapters.FRIEND_REQUEST_ITEM_TYPE
import com.sudox.messenger.android.friends.adapters.FriendsAdapter
import com.sudox.messenger.android.friends.vos.FriendVO

class FriendsSortingCallback(
        friendsAdapter: FriendsAdapter,
        headerType: Int
) : ViewListCallback<FriendVO>(friendsAdapter, headerType) {

    override fun compare(oldItem: FriendVO, newItem: FriendVO): Int {
        return if (headerType == FRIEND_REQUEST_ITEM_TYPE) {
            -oldItem.requestTime.compareTo(newItem.requestTime)
        } else {
            oldItem.name.compareTo(newItem.name)
        }
    }
}