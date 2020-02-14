package com.sudox.messenger.android.people.peopletab.callbacks

import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.design.viewlist.ViewListCallback
import com.sudox.messenger.android.people.peopletab.adapters.MAYBE_YOU_KNOW_TAG
import com.sudox.messenger.android.people.peopletab.vos.MaybeYouKnowVO

class MaybeYouKnowSortingCallback(
        viewListAdapter: ViewListAdapter<*>
) : ViewListCallback<MaybeYouKnowVO>(viewListAdapter, MAYBE_YOU_KNOW_TAG) {

    override fun compare(first: MaybeYouKnowVO, second: MaybeYouKnowVO): Int {
        return -first.mutualCount.compareTo(second.mutualCount)
    }
}