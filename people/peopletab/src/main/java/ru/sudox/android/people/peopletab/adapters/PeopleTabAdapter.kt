package ru.sudox.android.people.peopletab.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.design.viewlist.ViewList
import ru.sudox.design.viewlist.ViewListAdapter
import ru.sudox.design.viewlist.sortItems
import ru.sudox.design.viewlist.vos.ViewListHeaderVO
import ru.sudox.android.people.common.views.HorizontalPeopleItemView
import ru.sudox.android.people.peopletab.R
import ru.sudox.android.people.peopletab.callbacks.AddedFriendsSortingCallback
import ru.sudox.android.people.peopletab.callbacks.FriendRequestSortingCallback
import ru.sudox.android.people.peopletab.callbacks.SubscriptionsSortingCallback
import ru.sudox.android.people.peopletab.createMaybeYouKnowRecyclerView
import ru.sudox.android.people.peopletab.vos.AddedFriendVO
import ru.sudox.android.people.peopletab.vos.FriendRequestVO
import ru.sudox.android.people.peopletab.vos.SubscriptionVO
import ru.sudox.android.people.peopletab.vos.headers.AddedFriendsHeaderVO
import ru.sudox.android.people.peopletab.vos.headers.FRIENDS_OPTION_TAG
import ru.sudox.android.people.peopletab.vos.headers.FriendRequestsHeaderVO
import ru.sudox.android.people.peopletab.vos.headers.MaybeYouKnowHeaderVO
import ru.sudox.android.people.peopletab.vos.headers.SUBSCRIPTIONS_OPTION_TAG

const val FRIEND_REQUESTS_HEADER_TYPE = 0
const val MAYBE_YOU_KNOW_HEADER_TYPE = 1
const val ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE = 2

const val FRIEND_REQUEST_VIEW_TYPE = 0
const val MAYBE_YOU_KNOW_LIST_VIEW_TYPE = 1
const val ADDED_FRIEND_VIEW_TYPE = 2
const val SUBSCRIPTION_VIEW_TYPE = 3

/**
 * Адаптер для вкладки "People".
 */
class PeopleTabAdapter(
        val glide: GlideRequests
) : ViewListAdapter<RecyclerView.ViewHolder>() {

    override var headersVOs: Array<ViewListHeaderVO>? = arrayOf(
            FriendRequestsHeaderVO(),
            MaybeYouKnowHeaderVO(),
            AddedFriendsHeaderVO()
    )

    var viewPool = RecyclerView.RecycledViewPool()
    val addedFriendsVOs: SortedList<AddedFriendVO> = SortedList(AddedFriendVO::class.java, AddedFriendsSortingCallback(this))
    val friendsRequestsVOs: SortedList<FriendRequestVO> = SortedList(FriendRequestVO::class.java, FriendRequestSortingCallback(this))
    val subscriptionsVOs: SortedList<SubscriptionVO> = SortedList(SubscriptionVO::class.java, SubscriptionsSortingCallback(this))
    val maybeYouKnowAdapter = MaybeYouKnowAdapter(glide)

    override var viewList: ViewList? = null
        set(value) {
            field = value?.apply {
                setRecycledViewPool(viewPool)
                setItemViewCacheSize(20)
                setHasFixedSize(true)
            }
        }

    init {
        sectionChangedCallback = { headerType: Int, itemsCountBeforeChanging: Int, vo: ViewListHeaderVO ->
            if (vo.selectedToggleTag == FRIENDS_OPTION_TAG) {
                notifyChangedSectionDataChanged(headerType, itemsCountBeforeChanging, addedFriendsVOs.size())
            } else if (vo.selectedToggleTag == SUBSCRIPTIONS_OPTION_TAG) {
                notifyChangedSectionDataChanged(headerType, itemsCountBeforeChanging, subscriptionsVOs.size())
            }
        }

        sortingTypeChangedCallback = { headerType: Int, itemsCountBeforeChanging: Int, vo: ViewListHeaderVO ->
            toggleLoading(ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE, true, clearLoading = true)

            viewList!!.handler.postDelayed({
                if (vo.selectedToggleTag == FRIENDS_OPTION_TAG) {
                    addedFriendsVOs.sortItems()
                    notifyChangedSectionDataChanged(headerType, itemsCountBeforeChanging, addedFriendsVOs.size())
                } else if (vo.selectedToggleTag == SUBSCRIPTIONS_OPTION_TAG) {
                    subscriptionsVOs.sortItems()
                    notifyChangedSectionDataChanged(headerType, itemsCountBeforeChanging, subscriptionsVOs.size())
                }
            }, 3000L)
        }
    }

    override fun createItemHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType != MAYBE_YOU_KNOW_LIST_VIEW_TYPE) {
            PeopleViewHolder(HorizontalPeopleItemView(viewList!!.context))
        } else {
            ListViewHolder(createMaybeYouKnowRecyclerView(viewList!!.context).also { list ->
                list.setRecycledViewPool(viewPool)
                list.adapter = maybeYouKnowAdapter.apply {
                    viewList = list
                }
            })
        }
    }

    override fun bindItemHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? PeopleViewHolder)?.view?.setVO(when (getItemType(position)) {
            FRIEND_REQUEST_VIEW_TYPE -> friendsRequestsVOs
            ADDED_FRIEND_VIEW_TYPE -> addedFriendsVOs
            else -> subscriptionsVOs
        }[recalculatePositionRelativeHeader(position)], glide)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)

        if (holder is PeopleViewHolder) {
            holder.view.setVO(null, glide)
        }
    }

    override fun getItemType(position: Int): Int {
        var current = 0

        if (friendsRequestsVOs.size() > 0) {
            current += getItemsCountAfterHeaderConsiderVisibility(FRIEND_REQUESTS_HEADER_TYPE) + 1

            val headerVO = headersVOs!![FRIEND_REQUESTS_HEADER_TYPE]

            if (!headerVO.isItemsHidden && headerVO.isContentLoading) {
                current++
            }
        }

        if (position < current) {
            return FRIEND_REQUEST_VIEW_TYPE
        }

        if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
            current += getItemsCountAfterHeaderConsiderVisibility(MAYBE_YOU_KNOW_HEADER_TYPE) + 1
        }

        if (position < current) {
            return MAYBE_YOU_KNOW_LIST_VIEW_TYPE
        }

        val addedFriendsOrSubscriptionsCount = getItemsCountAfterHeaderConsiderVisibility(ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE)

        if (addedFriendsOrSubscriptionsCount > 0) {
            current += addedFriendsOrSubscriptionsCount + 1
        }

        if (position < current) {
            return if (headersVOs!![ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE].selectedToggleTag == FRIENDS_OPTION_TAG) {
                ADDED_FRIEND_VIEW_TYPE
            } else {
                SUBSCRIPTION_VIEW_TYPE
            }
        }

        return 0
    }

    override fun getHeaderByPosition(position: Int): ViewListHeaderVO? {
        if (position == 0) {
            if (friendsRequestsVOs.size() > 0) {
                return headersVOs!![FRIEND_REQUESTS_HEADER_TYPE]
            } else if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
                return headersVOs!![MAYBE_YOU_KNOW_HEADER_TYPE]
            } else if (addedFriendsVOs.size() > 0 || friendsRequestsVOs.size() > 0) {
                return headersVOs!![ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE]
            }
        }

        var sum = 0

        if (friendsRequestsVOs.size() > 0) {
            sum++

            if (!headersVOs!![FRIEND_REQUESTS_HEADER_TYPE].isItemsHidden) {
                sum += friendsRequestsVOs.size()
            }

            if (headersVOs!![FRIEND_REQUESTS_HEADER_TYPE].isLoaderShowing()) {
                sum++
            }
        }

        if (position == sum) {
            if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
                return headersVOs!![MAYBE_YOU_KNOW_HEADER_TYPE]
            } else if (addedFriendsVOs.size() > 0 || friendsRequestsVOs.size() > 0) {
                return headersVOs!![ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE]
            }
        }

        if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
            sum++

            if (!headersVOs!![MAYBE_YOU_KNOW_HEADER_TYPE].isItemsHidden) {
                sum++
            }
        }

        if (sum == position) {
            return headersVOs!![ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE]
        }

        return null
    }

    override fun getPositionForNewHeader(type: Int): Int {
        if (type == FRIEND_REQUESTS_HEADER_TYPE) {
            return 0
        }

        var position = 0

        if (friendsRequestsVOs.size() > 0) {
            position++

            if (!headersVOs!![FRIEND_REQUESTS_HEADER_TYPE].isItemsHidden) {
                position += friendsRequestsVOs.size()
            }
        }

        if (type == MAYBE_YOU_KNOW_HEADER_TYPE) {
            return position
        }

        if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
            position++

            if (!headersVOs!![MAYBE_YOU_KNOW_HEADER_TYPE].isItemsHidden) {
                position++
            }
        }

        return position
    }

    override fun getItemMargin(position: Int): Int {
        return viewList!!.context.resources.getDimensionPixelSize(R.dimen.peopletab_items_margin)
    }

    override fun getItemsCountAfterHeader(type: Int): Int {
        return if (type == FRIEND_REQUESTS_HEADER_TYPE) {
            friendsRequestsVOs.size()
        } else if (type == MAYBE_YOU_KNOW_HEADER_TYPE) {
            if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
                1
            } else {
                0
            }
        } else {
            if (headersVOs!![ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE].selectedToggleTag == FRIENDS_OPTION_TAG) {
                addedFriendsVOs.size()
            } else {
                subscriptionsVOs.size()
            }
        }
    }

    override fun getHeaderTypeByItemType(itemType: Int): Int {
        return if (itemType == FRIEND_REQUEST_VIEW_TYPE) {
            FRIEND_REQUESTS_HEADER_TYPE
        } else if (itemType == MAYBE_YOU_KNOW_LIST_VIEW_TYPE) {
            MAYBE_YOU_KNOW_HEADER_TYPE
        } else {
            ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE
        }
    }

    override fun getHeadersCount(): Int {
        var count = 0

        if (friendsRequestsVOs.size() > 0) {
            count++
        }

        if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
            count++
        }

        if (addedFriendsVOs.size() > 0 || subscriptionsVOs.size() > 0) {
            count++
        }

        return count
    }

    class PeopleViewHolder(val view: HorizontalPeopleItemView) : RecyclerView.ViewHolder(view)
    class ListViewHolder(val view: ViewList) : RecyclerView.ViewHolder(view)
}