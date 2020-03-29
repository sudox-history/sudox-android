package com.sudox.messenger.android.people.peopletab

import android.content.Context
import com.sudox.design.viewlist.ViewList
import com.sudox.messenger.android.core.fragments.ViewListFragment
import com.sudox.messenger.android.core.tabs.TabsChildFragment
import com.sudox.messenger.android.people.common.vos.SEEN_TIME_ONLINE
import com.sudox.messenger.android.people.peopletab.adapters.PeopleTabAdapter
import com.sudox.messenger.android.people.peopletab.vos.AddedFriendVO
import com.sudox.messenger.android.people.peopletab.vos.FriendRequestVO
import com.sudox.messenger.android.people.peopletab.vos.MaybeYouKnowVO
import com.sudox.messenger.android.people.peopletab.vos.appbar.PeopleTabAppBarVO

class PeopleTabFragment : ViewListFragment<PeopleTabAdapter>(), TabsChildFragment {

    init {
        appBarVO = PeopleTabAppBarVO()
    }

    override fun getAdapter(viewList: ViewList): PeopleTabAdapter {
        return PeopleTabAdapter().apply {
            friendsRequestsVOs.apply {
                add(FriendRequestVO(2, "undefined.7887", SEEN_TIME_ONLINE, 2L, "Привет, как дела?", 1L))
            }

            maybeYouKnowAdapter.maybeYouKnowVOs.apply {
                add(MaybeYouKnowVO(5, "andy", SEEN_TIME_ONLINE, 5, 21))
                add(MaybeYouKnowVO(1, "kerjen", 1L, 1, 18))
            }

            addedFriendsVOs.apply {
                add(AddedFriendVO(4, "Максим Митюшкин", SEEN_TIME_ONLINE, 4, "Я строю новый ЦОД каждый день", 1))
                add(AddedFriendVO(6, "Jeremy Clarkson", 1583520378000L, 6, "I am a still small voice of calm and reason.", 1))
            }

            subscriptionsVOs.apply {}
        }
    }

    override fun getTitle(context: Context): String {
        return context.getString(R.string.people)
    }
}