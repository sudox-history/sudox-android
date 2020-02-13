package com.sudox.messenger.android.people.peopletab.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.people.common.views.HorizontalPeopleItemView
import com.sudox.messenger.android.people.peopletab.callbacks.AddedFriendsSortingCallback
import com.sudox.messenger.android.people.peopletab.callbacks.FriendRequestSortingCallback
import com.sudox.messenger.android.people.peopletab.vos.AddedFriendVO
import com.sudox.messenger.android.people.peopletab.vos.FriendRequestVO
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

    var addedFriendsVOs: SortedList<AddedFriendVO>
    var friendsRequestsVO: SortedList<FriendRequestVO>

    init {
        val addedFriendsHeaderVO = headersVO[ADDED_FRIENDS_HEADER_TAG]!!
        val addedFriendsSortType = addedFriendsHeaderVO.selectedFunctionButtonToggleTags!![addedFriendsHeaderVO.selectedToggleTag]!!
        val addedFriendsCallback = AddedFriendsSortingCallback(this, addedFriendsSortType)

        addedFriendsVOs = SortedList(AddedFriendVO::class.java, addedFriendsCallback)
        friendsRequestsVO = SortedList(FriendRequestVO::class.java, FriendRequestSortingCallback(this))
    }

    override fun createItemHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO()
    }

    override fun bindItemHolder(holder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class ViewHolder(val view: HorizontalPeopleItemView) : RecyclerView.ViewHolder(view)
}