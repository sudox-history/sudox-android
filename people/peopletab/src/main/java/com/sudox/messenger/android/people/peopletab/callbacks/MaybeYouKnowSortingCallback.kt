package com.sudox.messenger.android.people.peopletab.callbacks

import com.sudox.design.viewlist.ViewListCallback
import com.sudox.messenger.android.people.peopletab.adapters.MaybeYouKnowAdapter
import com.sudox.messenger.android.people.peopletab.vos.MaybeYouKnowVO

class MaybeYouKnowSortingCallback(
        maybeYouKnowAdapter: MaybeYouKnowAdapter
) : ViewListCallback<MaybeYouKnowVO>(maybeYouKnowAdapter, 0) {

    override fun compare(oldItem: MaybeYouKnowVO, newItem: MaybeYouKnowVO): Int {
        return -oldItem.mutualFriendsCount.compareTo(newItem.mutualFriendsCount)
    }
}