package com.sudox.messenger.android.people.peopletab.callbacks

import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.design.viewlist.ViewListCallback
import com.sudox.messenger.android.people.peopletab.adapters.MAYBE_YOU_KNOW_HEADER_TYPE
import com.sudox.messenger.android.people.peopletab.vos.MaybeYouKnowVO

/**
 * Кэллбэк для сортированного списка блока "Maybe you know".
 *
 * @param viewListAdapter Адаптер ViewList'а, в которой находятся элементы для сортировки
 */
class MaybeYouKnowSortingCallback(
        viewListAdapter: ViewListAdapter<*>
) : ViewListCallback<MaybeYouKnowVO>(viewListAdapter, MAYBE_YOU_KNOW_HEADER_TYPE) {

    override fun compare(first: MaybeYouKnowVO, second: MaybeYouKnowVO): Int {
        return -first.mutualCount.compareTo(second.mutualCount)
    }
}