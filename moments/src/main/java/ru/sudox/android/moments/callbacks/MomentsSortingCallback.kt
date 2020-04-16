package ru.sudox.android.moments.callbacks

import ru.sudox.design.viewlist.ViewListAdapter
import ru.sudox.design.viewlist.ViewListCallback
import ru.sudox.android.moments.vos.impl.MomentVO

class MomentsSortingCallback(
        viewListAdapter: ViewListAdapter<*>,
        offset: Int = 0
) : ViewListCallback<MomentVO>(viewListAdapter, offset = offset) {

    override fun compare(first: MomentVO, second: MomentVO): Int {
        return -first.publishTime.compareTo(second.publishTime)
    }
}