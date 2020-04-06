package ru.sudox.android.people.peopletab.callbacks

import ru.sudox.design.viewlist.ViewListAdapter
import ru.sudox.design.viewlist.ViewListCallback
import ru.sudox.android.people.peopletab.adapters.ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE
import ru.sudox.android.people.peopletab.vos.SubscriptionVO
import ru.sudox.android.people.peopletab.vos.headers.FAVORITE_OPTION_TAG
import ru.sudox.android.people.peopletab.vos.headers.POPULAR_OPTION_TAG
import ru.sudox.android.people.peopletab.vos.headers.SUBSCRIPTIONS_OPTION_TAG

/**
 * Кэллбэк для сортированного списка подписок.
 *
 * @param viewListAdapter Адаптер ViewList'а, в которой находятся элементы для сортировки
 */
class SubscriptionsSortingCallback(
        viewListAdapter: ViewListAdapter<*>
) : ViewListCallback<SubscriptionVO>(viewListAdapter, ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE) {

    override fun compare(first: SubscriptionVO, second: SubscriptionVO): Int {
        val sortingType = viewListAdapter.getSortingTypeByHeader(headerType, SUBSCRIPTIONS_OPTION_TAG)

        return if (sortingType == FAVORITE_OPTION_TAG) {
            -first.favorite.compareTo(second.favorite)
        } else if (sortingType == POPULAR_OPTION_TAG) {
            -first.popular.compareTo(second.popular)
        } else {
            first.userName.compareTo(second.userName)
        }
    }
}