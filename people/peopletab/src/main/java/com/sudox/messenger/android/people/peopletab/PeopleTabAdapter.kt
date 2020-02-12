package com.sudox.messenger.android.people.peopletab

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.people.common.views.HorizontalPeopleItemView
import com.sudox.messenger.android.people.peopletab.vos.AddedFriendVO
import com.sudox.messenger.android.people.peopletab.vos.headers.AddedFriendsHeaderVO
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
        holder.view.vo = AddedFriendVO(0, "Mr. Robot", 1499, 1)
    }

    override fun getHeaderByPosition(position: Int): ViewListHeaderVO? {
        return if (position == 0) {
            headersVO[MAYBE_YOU_KNOW_HEADER_TAG]
        } else {
            null
        }
    }

    override fun getItemsCountAfterHeader(type: Int): Int {
        return 1
    }

    override fun getHeadersCount(): Int {
        return 1
    }

    override fun getItemsCount(): Int {
        return if (headersVO[MAYBE_YOU_KNOW_HEADER_TAG]!!.isItemsHidden) {
            0
        } else {
            1
        }
    }

    class ViewHolder(val view: HorizontalPeopleItemView) : RecyclerView.ViewHolder(view)
}