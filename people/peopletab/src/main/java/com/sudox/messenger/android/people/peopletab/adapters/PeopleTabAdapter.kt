package com.sudox.messenger.android.people.peopletab.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.people.common.views.HorizontalPeopleItemView
import com.sudox.messenger.android.people.peopletab.R
import com.sudox.messenger.android.people.peopletab.callbacks.AddedFriendsSortingCallback
import com.sudox.messenger.android.people.peopletab.callbacks.FriendRequestSortingCallback
import com.sudox.messenger.android.people.peopletab.callbacks.SubscriptionsSortingCallback
import com.sudox.messenger.android.people.peopletab.createMaybeYouKnowRecyclerView
import com.sudox.messenger.android.people.peopletab.vos.AddedFriendVO
import com.sudox.messenger.android.people.peopletab.vos.FriendRequestVO
import com.sudox.messenger.android.people.peopletab.vos.SubscriptionVO
import com.sudox.messenger.android.people.peopletab.vos.headers.AddedFriendsHeaderVO
import com.sudox.messenger.android.people.peopletab.vos.headers.FRIENDS_OPTION_TAG
import com.sudox.messenger.android.people.peopletab.vos.headers.FriendRequestsHeaderVO
import com.sudox.messenger.android.people.peopletab.vos.headers.MaybeYouKnowHeaderVO
import com.sudox.messenger.android.people.peopletab.vos.headers.SUBSCRIPTIONS_OPTION_TAG

const val FRIEND_REQUESTS_HEADER_TYPE = 1
const val MAYBE_YOU_KNOW_HEADER_TYPE = 2
const val ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE = 3

const val FRIEND_REQUEST_VIEW_TYPE = 1
const val MAYBE_YOU_KNOW_VIEW_TYPE = 2
const val ADDED_FRIEND_VIEW_TYPE = 3
const val SUBSCRIPTION_VIEW_TYPE = 4

class PeopleTabAdapter(
        val headersVO: HashMap<Int, ViewListHeaderVO> = hashMapOf(
                FRIEND_REQUESTS_HEADER_TYPE to FriendRequestsHeaderVO(),
                MAYBE_YOU_KNOW_HEADER_TYPE to MaybeYouKnowHeaderVO(),
                ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE to AddedFriendsHeaderVO())
) : ViewListAdapter<RecyclerView.ViewHolder>(headersVO) {

    val addedFriendsVOs: SortedList<AddedFriendVO>
    val friendsRequestsVO: SortedList<FriendRequestVO>
    val subscriptionsVOs: SortedList<SubscriptionVO>
    val maybeYouKnowAdapter = MaybeYouKnowAdapter()

    init {
        val addedFriendsHeaderVO = headersVO[ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE]!!
        val addedFriendsSortType = addedFriendsHeaderVO.selectedFunctionButtonToggleTags!![FRIENDS_OPTION_TAG]!!
        val addedFriendsCallback = AddedFriendsSortingCallback(this, addedFriendsSortType)

        addedFriendsVOs = SortedList(AddedFriendVO::class.java, addedFriendsCallback)

        val subscriptionsSortType = addedFriendsHeaderVO.selectedFunctionButtonToggleTags!![SUBSCRIPTIONS_OPTION_TAG]!!
        val subscriptionsCallback = SubscriptionsSortingCallback(this, subscriptionsSortType)

        subscriptionsVOs = SortedList(SubscriptionVO::class.java, subscriptionsCallback)
        friendsRequestsVO = SortedList(FriendRequestVO::class.java, FriendRequestSortingCallback(this))

        sectionChangedCallback = { headerType: Int, itemsCountBeforeChanging: Int, vo: ViewListHeaderVO ->
            if (vo.selectedToggleTag == FRIENDS_OPTION_TAG) {
                notifyChangedSectionDataChanged(headerType, itemsCountBeforeChanging, addedFriendsVOs.size())
            } else if (vo.selectedToggleTag == SUBSCRIPTIONS_OPTION_TAG) {
                notifyChangedSectionDataChanged(headerType, itemsCountBeforeChanging, subscriptionsVOs.size())
            }
        }
    }

    override fun createItemHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType != MAYBE_YOU_KNOW_VIEW_TYPE) {
            PeopleViewHolder(HorizontalPeopleItemView(viewList!!.context))
        } else {
            ListViewHolder(createMaybeYouKnowRecyclerView(viewList!!.context).also { list ->
                list.adapter = maybeYouKnowAdapter.apply {
                    viewList = list
                }
            })
        }
    }

    override fun bindItemHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? PeopleViewHolder)?.view?.vo = when (getItemType(position)) {
            FRIEND_REQUEST_VIEW_TYPE -> friendsRequestsVO
            ADDED_FRIEND_VIEW_TYPE -> addedFriendsVOs
            else -> subscriptionsVOs
        }[recalculatePositionRelativeHeader(position)]
    }

    override fun getItemType(position: Int): Int {
        var current = 0

        if (friendsRequestsVO.size() > 0) {
            current += getItemsCountAfterHeaderConsiderVisibility(FRIEND_REQUESTS_HEADER_TYPE) + 1

            if (headersVO[FRIEND_REQUESTS_HEADER_TYPE]!!.isContentLoading) {
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
            return MAYBE_YOU_KNOW_VIEW_TYPE
        }

        val addedFriendsOrSubscriptionsCount = getItemsCountAfterHeaderConsiderVisibility(ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE)

        if (addedFriendsOrSubscriptionsCount > 0) {
            current += addedFriendsOrSubscriptionsCount + 1
        }

        if (position < current) {
            return if (headersVO[ADDED_FRIEND_VIEW_TYPE]!!.selectedToggleTag == FRIENDS_OPTION_TAG) {
                ADDED_FRIEND_VIEW_TYPE
            } else {
                SUBSCRIPTION_VIEW_TYPE
            }
        }

        return 0
    }

    override fun getHeaderByPosition(position: Int): ViewListHeaderVO? {
        if (position == 0) {
            if (friendsRequestsVO.size() > 0) {
                return headersVO[FRIEND_REQUESTS_HEADER_TYPE]
            } else if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
                return headersVO[MAYBE_YOU_KNOW_HEADER_TYPE]
            } else if (addedFriendsVOs.size() > 0 || friendsRequestsVO.size() > 0) {
                return headersVO[ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE]
            }
        }

        var sum = 0

        if (friendsRequestsVO.size() > 0) {
            sum++

            if (!headersVO[FRIEND_REQUESTS_HEADER_TYPE]!!.isItemsHidden) {
                sum += friendsRequestsVO.size()
            }

            if (headersVO[FRIEND_REQUESTS_HEADER_TYPE]!!.isContentLoading) {
                sum++
            }
        }

        if (position == sum) {
            if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
                return headersVO[MAYBE_YOU_KNOW_HEADER_TYPE]
            } else if (addedFriendsVOs.size() > 0 || friendsRequestsVO.size() > 0) {
                return headersVO[ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE]
            }
        }

        if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
            sum++

            if (!headersVO[MAYBE_YOU_KNOW_HEADER_TYPE]!!.isItemsHidden) {
                sum++
            }
        }

        if (sum == position) {
            return headersVO[ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE]
        }

        return null
    }

    override fun getPositionForNewHeader(type: Int): Int {
        if (type == FRIEND_REQUESTS_HEADER_TYPE) {
            return 0
        }

        var position = 0

        if (friendsRequestsVO.size() > 0) {
            position++

            if (!headersVO[FRIEND_REQUESTS_HEADER_TYPE]!!.isItemsHidden) {
                position += friendsRequestsVO.size()
            }
        }

        if (type == MAYBE_YOU_KNOW_HEADER_TYPE) {
            return position
        }

        if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
            position++

            if (!headersVO[MAYBE_YOU_KNOW_HEADER_TYPE]!!.isItemsHidden) {
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
            friendsRequestsVO.size()
        } else if (type == MAYBE_YOU_KNOW_HEADER_TYPE) {
            if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
                1
            } else {
                0
            }
        } else {
            if (headersVO[ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE]!!.selectedToggleTag == FRIENDS_OPTION_TAG) {
                addedFriendsVOs.size()
            } else {
                subscriptionsVOs.size()
            }
        }
    }

    override fun getHeadersCount(): Int {
        var count = 0

        if (subscriptionsVOs.size() > 0) {
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