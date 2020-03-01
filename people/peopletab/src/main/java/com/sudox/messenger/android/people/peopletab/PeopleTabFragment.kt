package com.sudox.messenger.android.people.peopletab

import android.content.Context
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.design.viewlist.ViewList
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.fragments.ViewListFragment
import com.sudox.messenger.android.core.tabs.TabsChildFragment
import com.sudox.messenger.android.people.common.vos.SEEN_TIME_ONLINE
import com.sudox.messenger.android.people.peopletab.adapters.PeopleTabAdapter
import com.sudox.messenger.android.people.peopletab.vos.AddedFriendVO
import com.sudox.messenger.android.people.peopletab.vos.FriendRequestVO
import com.sudox.messenger.android.people.peopletab.vos.MaybeYouKnowVO
import com.sudox.messenger.android.people.peopletab.vos.SubscriptionVO

class PeopleTabFragment : ViewListFragment<PeopleTabAdapter>(), TabsChildFragment, ApplicationBarListener {

    override fun prepareToShowing(coreFragment: CoreFragment) {
        super.prepareToShowing(coreFragment)

        applicationBarManager!!.let {
            it.setListener(this)
            it.toggleIconButtonAtEnd(R.drawable.ic_search)
        }
    }

    override fun getAdapter(viewList: ViewList): PeopleTabAdapter {
        return PeopleTabAdapter().apply {
            friendsRequestsVOs.apply {
                add(FriendRequestVO(4, "Pidor Request 1", 145, 1, null, 2L))
                add(FriendRequestVO(5, "Pidor Request 2", 145, 1, "Hello, Pidor", 1L))
                add(FriendRequestVO(5, "Pidor Request 3", 145, 2, "Hello, Pidor", 1L))
                add(FriendRequestVO(5, "Pidor Request 4", 145, 1, "Hello, Pidor", 1L))
                add(FriendRequestVO(5, "Pidor Request 5", 145, 1, "Hello, Pidor", 1L))
                add(FriendRequestVO(5, "Pidor Request 6", 145, 2, "Hello, Pidor", 1L))
                add(FriendRequestVO(6, "Pidor Request 7", 146, 1, null, 3L))
            }

            subscriptionsVOs.apply {
                add(SubscriptionVO(1, "TheMax", SEEN_TIME_ONLINE, 3, "Suka, rabotai", 1, 1))
                add(SubscriptionVO(1, "Not online", 45, 3, "Suka, rabotai", 2, 2))
            }

            addedFriendsVOs.apply {
                add(AddedFriendVO(1, "Pidor Added 1", 145, 3, 1))
                add(AddedFriendVO(2, "Pidor Added 2", 145, 3, 2))
                add(AddedFriendVO(3, "Pidor Added 3", 145, 3, 3))
                add(AddedFriendVO(4, "Pidor Added 4", SEEN_TIME_ONLINE, 3, 4))
                add(AddedFriendVO(5, "Pidor Added 5", 145, 3, 5))
                add(AddedFriendVO(6, "Pidor Added 6", 145, 3, 6))
            }

            maybeYouKnowAdapter.maybeYouKnowVOs.apply {
                add(MaybeYouKnowVO(1, "Pidor 1", SEEN_TIME_ONLINE, 3, 15))
                add(MaybeYouKnowVO(2, "Pidor 2", SEEN_TIME_ONLINE, 3, 18))
                add(MaybeYouKnowVO(3, "Pidor 3", SEEN_TIME_ONLINE, 3, 18))
                add(MaybeYouKnowVO(4, "Pidor 4", SEEN_TIME_ONLINE, 3, 18))
                add(MaybeYouKnowVO(5, "Pidor 5", SEEN_TIME_ONLINE, 3, 18))
                add(MaybeYouKnowVO(6, "Pidor 6", SEEN_TIME_ONLINE, 3, 18))
                add(MaybeYouKnowVO(7, "Pidor 7", SEEN_TIME_ONLINE, 3, 18))
                add(MaybeYouKnowVO(8, "Pidor 8", SEEN_TIME_ONLINE, 3, 18))
                add(MaybeYouKnowVO(9, "Pidor 9", SEEN_TIME_ONLINE, 3, 18))
            }
        }
    }

    override fun getTitle(context: Context): String? {
        return context.getString(R.string.people)
    }

    override fun onButtonClicked(tag: Int) {
    }
}