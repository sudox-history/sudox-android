package ru.sudox.android.people.peopletab.callbacks

import ru.sudox.design.viewlist.ViewListAdapter
import ru.sudox.design.viewlist.ViewListCallback
import ru.sudox.android.people.peopletab.adapters.FRIEND_REQUESTS_HEADER_TYPE
import ru.sudox.android.people.peopletab.vos.FriendRequestVO

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