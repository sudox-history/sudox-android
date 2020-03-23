package com.sudox.messenger.android.moments.callbacks

import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.design.viewlist.ViewListCallback
import com.sudox.messenger.android.moments.vos.MomentVO

class MomentsSortingCallback(
        viewListAdapter: ViewListAdapter<*>,
        offset: Int = 0
) : ViewListCallback<MomentVO>(viewListAdapter, offset = offset) {

    override fun compare(first: MomentVO, second: MomentVO): Int {
        return -first.publishTime.compareTo(second.publishTime)
    }
}