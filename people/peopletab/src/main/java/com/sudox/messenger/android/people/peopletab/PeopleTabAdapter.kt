package com.sudox.messenger.android.people.peopletab

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.people.common.views.HorizontalPeopleItemView
import com.sudox.messenger.android.people.peopletab.vos.AddedFriendVO
import com.sudox.messenger.android.people.peopletab.vos.headers.AddedFriendsHeaderVO
import com.sudox.messenger.android.people.peopletab.vos.headers.FRIENDS_OPTION_TAG
import com.sudox.messenger.android.people.peopletab.vos.headers.FriendRequestsHeaderVO
import com.sudox.messenger.android.people.peopletab.vos.headers.MaybeYouKnowHeaderVO

const val FRIEND_REQUESTS_HEADER_TAG = 0
const val MAYBE_YOU_KNOW_HEADER_TAG = 1
const val ADDED_FRIENDS_HEADER_TAG = 2

class PeopleTabAdapter(
        val viewList: ViewList,
        val headersVO: HashMap<Int, ViewListHeaderVO> = hashMapOf(
                FRIEND_REQUESTS_HEADER_TAG to FriendRequestsHeaderVO(),
                MAYBE_YOU_KNOW_HEADER_TAG to MaybeYouKnowHeaderVO(),
                ADDED_FRIENDS_HEADER_TAG to AddedFriendsHeaderVO())
) : ViewListAdapter<PeopleTabAdapter.ViewHolder>(viewList, headersVO) {

    override fun createItemHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(HorizontalPeopleItemView(viewList.context))
    }

    override fun bindItemHolder(holder: ViewHolder, position: Int) {
        if (getItemType(position) == 1) {
            if (headersVO[ADDED_FRIENDS_HEADER_TAG]!!.selectedToggleTag == FRIENDS_OPTION_TAG) {
                holder.view.vo = AddedFriendVO(0, "Mr. Robot Friend $position", 1499, 1)
            } else {
                holder.view.vo = AddedFriendVO(0, "Mr. Robot Subscription $position", 1499, 1)
            }
        }
    }

    override fun getItemType(position: Int): Int {
        return if (position > 0) {
            1
        } else {
            0
        }
    }

    override fun getHeaderByPosition(position: Int): ViewListHeaderVO? {
        return if (position == 0) {
            headersVO[ADDED_FRIENDS_HEADER_TAG]
        } else {
            null
        }
    }

    override fun getItemsCountAfterHeader(type: Int): Int {
        return if (headersVO[ADDED_FRIENDS_HEADER_TAG]!!.isItemsHidden) {
            0
        } else if (headersVO[ADDED_FRIENDS_HEADER_TAG]!!.selectedToggleTag == FRIENDS_OPTION_TAG) {
            1
        } else {
            2
        }
    }

    override fun getHeadersCount(): Int {
        return 1
    }

    override fun getItemsCount(): Int {
        return if (headersVO[ADDED_FRIENDS_HEADER_TAG]!!.isItemsHidden) {
            0
        } else if (headersVO[ADDED_FRIENDS_HEADER_TAG]!!.selectedToggleTag == FRIENDS_OPTION_TAG) {
            1
        } else {
            2
        }
    }

    class ViewHolder(val view: HorizontalPeopleItemView) : RecyclerView.ViewHolder(view)
}