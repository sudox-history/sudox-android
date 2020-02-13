package com.sudox.messenger.android.people.peopletab.callbacks

import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.design.viewlist.ViewListCallback
import com.sudox.messenger.android.people.peopletab.adapters.FRIEND_REQUESTS_HEADER_TAG
import com.sudox.messenger.android.people.peopletab.vos.FriendRequestVO

class FriendRequestSortingCallback(
        viewListAdapter: ViewListAdapter<*>
) : ViewListCallback<FriendRequestVO>(viewListAdapter, FRIEND_REQUESTS_HEADER_TAG) {

    override fun compare(first: FriendRequestVO, second: FriendRequestVO): Int {
        return -first.requestTime.compareTo(second.requestTime)
    }
}