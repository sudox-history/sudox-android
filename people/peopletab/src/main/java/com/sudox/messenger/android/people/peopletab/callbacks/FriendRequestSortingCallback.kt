package com.sudox.messenger.android.people.peopletab.callbacks

import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.design.viewlist.ViewListCallback
import com.sudox.messenger.android.people.peopletab.adapters.FRIEND_REQUESTS_HEADER_TYPE
import com.sudox.messenger.android.people.peopletab.vos.FriendRequestVO

/**
 * Кэллбэк для сортированного списка запросов в друзья.
 *
 * @param viewListAdapter Адаптер ViewList'а, в которой находятся элементы для сортировки
 */
class FriendRequestSortingCallback(
        viewListAdapter: ViewListAdapter<*>
) : ViewListCallback<FriendRequestVO>(viewListAdapter, FRIEND_REQUESTS_HEADER_TYPE) {

    override fun compare(first: FriendRequestVO, second: FriendRequestVO): Int {
        return -first.requestTime.compareTo(second.requestTime)
    }
}