package com.sudox.messenger.android.friends.callbacks

import com.sudox.design.viewlist.ViewListCallback
import com.sudox.messenger.android.friends.adapters.MaybeYouKnowAdapter
import com.sudox.messenger.android.friends.vos.MaybeYouKnowVO

class MaybeYouKnowSortingCallback(
        maybeYouKnowAdapter: MaybeYouKnowAdapter
) : ViewListCallback<MaybeYouKnowVO>(maybeYouKnowAdapter, 0) {

    override fun compare(oldItem: MaybeYouKnowVO, newItem: MaybeYouKnowVO): Int {
        return -oldItem.mutualFriendsCount.compareTo(newItem.mutualFriendsCount)
    }
}