package com.sudox.messenger.android.people.peopletab.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.people.common.views.HorizontalPeopleItemView
import com.sudox.messenger.android.people.common.vos.PeopleVO
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

const val FRIEND_REQUESTS_TAG = 1
const val MAYBE_YOU_KNOW_TAG = 2
const val ADDED_FRIENDS_AND_SUBSCRIPTIONS_TAG = 3

class PeopleTabAdapter(
        val headersVO: HashMap<Int, ViewListHeaderVO> = hashMapOf(
                FRIEND_REQUESTS_TAG to FriendRequestsHeaderVO(),
                MAYBE_YOU_KNOW_TAG to MaybeYouKnowHeaderVO(),
                ADDED_FRIENDS_AND_SUBSCRIPTIONS_TAG to AddedFriendsHeaderVO())
) : ViewListAdapter<RecyclerView.ViewHolder>(headersVO) {

    val addedFriendsVOs: SortedList<AddedFriendVO>
    val friendsRequestsVO: SortedList<FriendRequestVO>
    val subscriptionsVOs: SortedList<SubscriptionVO>
    val maybeYouKnowAdapter = MaybeYouKnowAdapter()

    init {
        val addedFriendsHeaderVO = headersVO[ADDED_FRIENDS_AND_SUBSCRIPTIONS_TAG]!!
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
        return if (viewType == MAYBE_YOU_KNOW_TAG) {
            ListViewHolder(createMaybeYouKnowRecyclerView(viewList!!.context).also { list ->
                list.adapter = maybeYouKnowAdapter.apply {
                    viewList = list
                }
            })
        } else {
            PeopleViewHolder(HorizontalPeopleItemView(viewList!!.context))
        }
    }

    override fun bindItemHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PeopleViewHolder) {
            val voPosition = recalculatePositionRelativeHeader(position)

            holder.view.vo = when (getItemViewType(position)) {
                FRIEND_REQUESTS_TAG -> friendsRequestsVO
                else -> getAddedFriendsOrSubscriptionsList()
            }[voPosition]
        }
    }

    override fun getItemType(position: Int): Int {
        val itemPosition = recalculatePosition(position)
        var itemsCount = friendsRequestsVO.size()

        if (itemPosition < itemsCount) {
            return FRIEND_REQUESTS_TAG
        }

        itemsCount += if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0 && !headersVO[MAYBE_YOU_KNOW_TAG]!!.isItemsHidden) {
            1
        } else {
            0
        }

        if (itemPosition < itemsCount) {
            return MAYBE_YOU_KNOW_TAG
        }

        itemsCount += getAddedFriendsOrSubscriptionsList().size()

        if (itemPosition < itemsCount) {
            return ADDED_FRIENDS_AND_SUBSCRIPTIONS_TAG
        }

        return 0
    }

    override fun getItemsCountAfterHeader(type: Int, ignoreHidden: Boolean): Int {
        return when (type) {
            FRIEND_REQUESTS_TAG -> friendsRequestsVO.size()
            MAYBE_YOU_KNOW_TAG -> if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0
                    && (ignoreHidden || !headersVO[MAYBE_YOU_KNOW_TAG]!!.isItemsHidden)) {
                1
            } else {
                0
            }
            else -> getAddedFriendsOrSubscriptionsList().size()
        }
    }

    override fun getItemMargin(position: Int): Int {
        return viewList!!.context.resources.getDimensionPixelSize(R.dimen.peopletab_items_margin)
    }

    override fun getHeaderByPosition(position: Int): ViewListHeaderVO? {
        val addedFriendsOrSubscriptionsList = getAddedFriendsOrSubscriptionsList()

        if (position == 0) {
            if (friendsRequestsVO.size() > 0) {
                return headersVO[FRIEND_REQUESTS_TAG]
            } else if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
                return headersVO[MAYBE_YOU_KNOW_TAG]
            } else if (addedFriendsOrSubscriptionsList.size() > 0) {
                return headersVO[ADDED_FRIENDS_AND_SUBSCRIPTIONS_TAG]
            }
        }

        var sum = 0

        if (friendsRequestsVO.size() > 0) {
            sum += friendsRequestsVO.size() + 1
        }

        if (position == sum) {
            if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
                return headersVO[MAYBE_YOU_KNOW_TAG]
            } else if (addedFriendsOrSubscriptionsList.size() > 0) {
                return headersVO[ADDED_FRIENDS_AND_SUBSCRIPTIONS_TAG]
            }
        }

        if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
            sum++

            if (!headersVO[MAYBE_YOU_KNOW_TAG]!!.isItemsHidden) {
                sum++
            }
        }

        if (sum == position) {
            return headersVO[ADDED_FRIENDS_AND_SUBSCRIPTIONS_TAG]
        }

        return null
    }

    override fun getPositionForNewHeader(type: Int): Int {
        if (type == FRIEND_REQUESTS_TAG) {
            return 0
        }

        var position = 0

        if (friendsRequestsVO.size() > 0) {
            position += friendsRequestsVO.size() + 1
        }

        if (type == MAYBE_YOU_KNOW_TAG) {
            return position
        }

        if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
            position++

            if (!headersVO[MAYBE_YOU_KNOW_TAG]!!.isItemsHidden) {
                position++
            }
        }

        return position
    }

    override fun getHeadersCount(): Int {
        var headersCount = 0

        if (friendsRequestsVO.size() > 0) {
            headersCount++
        }

        if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 1) {
            headersCount++
        }

        if (addedFriendsVOs.size() > 0 || subscriptionsVOs.size() > 0) {
            headersCount++
        }

        return headersCount
    }

    private fun getAddedFriendsOrSubscriptionsList(): SortedList<out PeopleVO> {
        return if (headersVO[ADDED_FRIENDS_AND_SUBSCRIPTIONS_TAG]!!.selectedToggleTag == FRIENDS_OPTION_TAG) {
            addedFriendsVOs
        } else {
            subscriptionsVOs
        }
    }

    class PeopleViewHolder(val view: HorizontalPeopleItemView) : RecyclerView.ViewHolder(view)
    class ListViewHolder(val view: ViewList) : RecyclerView.ViewHolder(view)
}