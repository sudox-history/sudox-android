package ru.sudox.android.people.peopletab.callbacks

import ru.sudox.design.viewlist.ViewListAdapter
import ru.sudox.design.viewlist.ViewListCallback
import ru.sudox.android.people.peopletab.adapters.ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE
import ru.sudox.android.people.peopletab.vos.AddedFriendVO
import ru.sudox.android.people.peopletab.vos.headers.FRIENDS_OPTION_TAG
import ru.sudox.android.people.peopletab.vos.headers.IMPORTANCE_OPTION_TAG
import ru.sudox.android.people.peopletab.vos.headers.ONLINE_OPTION_TAG

/**
 * Кэллбэк для сортированного списка добавленных друзей
 *
 * @param viewListAdapter Адаптер ViewList'а, в которой находятся элементы для сортировки
 */
class AddedFriendsSortingCallback(
        viewListAdapter: ViewListAdapter<*>
) : ViewListCallback<AddedFriendVO>(viewListAdapter, ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE) {

    override fun compare(first: AddedFriendVO, second: AddedFriendVO): Int {
        val sortingType = viewListAdapter.getSortingTypeByHeader(headerType, FRIENDS_OPTION_TAG)

        return if (sortingType == IMPORTANCE_OPTION_TAG) {
            -first.importance.compareTo(second.importance)
        } else if (sortingType == ONLINE_OPTION_TAG) {
            if (first.isUserOnline() && !second.isUserOnline()) {
                -1
            } else if (second.isUserOnline() && !first.isUserOnline()) {
                1
            } else if (second.isUserOnline() && first.isUserOnline()) {
                first.userName.compareTo(second.userName)
            } else {
                -first.seenTime.compareTo(second.seenTime)
            }
        } else {
            first.userName.compareTo(second.userName)
        }
    }
}