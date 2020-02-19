package com.sudox.messenger.android.people.peopletab

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.viewPager.ViewPagerFragment
import com.sudox.messenger.android.people.common.vos.SEEN_TIME_ONLINE
import com.sudox.messenger.android.people.peopletab.adapters.ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE
import com.sudox.messenger.android.people.peopletab.adapters.FRIEND_REQUESTS_HEADER_TYPE
import com.sudox.messenger.android.people.peopletab.adapters.MAYBE_YOU_KNOW_HEADER_TYPE
import com.sudox.messenger.android.people.peopletab.adapters.PeopleTabAdapter
import com.sudox.messenger.android.people.peopletab.vos.AddedFriendVO
import com.sudox.messenger.android.people.peopletab.vos.FriendRequestVO
import com.sudox.messenger.android.people.peopletab.vos.MaybeYouKnowVO
import com.sudox.messenger.android.people.peopletab.vos.SubscriptionVO
import kotlinx.android.synthetic.main.fragment_people_tab.peopleTabContentList

class PeopleTabFragment : CoreFragment(), ViewPagerFragment, ApplicationBarListener {

    private var adapter: PeopleTabAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_people_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PeopleTabAdapter().apply {
            viewList = peopleTabContentList

            friendsRequestsVOs.apply {
                add(FriendRequestVO(4, "Pidor Request 1", 145, 2, null, 2L))
                add(FriendRequestVO(5, "Pidor Request 2", 145, 2, "Hello, Pidor", 1L))
                add(FriendRequestVO(5, "Pidor Request 3", 145, 2, "Hello, Pidor", 1L))
                add(FriendRequestVO(5, "Pidor Request 4", 145, 2, "Hello, Pidor", 1L))
                add(FriendRequestVO(5, "Pidor Request 5", 145, 2, "Hello, Pidor", 1L))
                add(FriendRequestVO(5, "Pidor Request 6", 145, 2, "Hello, Pidor", 1L))
                add(FriendRequestVO(6, "Pidor Request 7", 146, 2, null, 3L))
            }

            subscriptionsVOs.apply {
                add(SubscriptionVO(1, "TheMax", SEEN_TIME_ONLINE, 45, "Suka, rabotai", 1, 1))
                add(SubscriptionVO(1, "Not online", 45, 45, "Suka, rabotai", 2, 2))
            }

            addedFriendsVOs.apply {
                add(AddedFriendVO(1, "Pidor Added 1", 145, 2, 1))
                add(AddedFriendVO(2, "Pidor Added 2", 145, 2, 2))
                add(AddedFriendVO(3, "Pidor Added 3", 145, 2, 3))
                add(AddedFriendVO(4, "Pidor Added 4", SEEN_TIME_ONLINE, 2, 4))
                add(AddedFriendVO(5, "Pidor Added 5", 145, 2, 5))
                add(AddedFriendVO(6, "Pidor Added 6", 145, 2, 6))
            }

            maybeYouKnowAdapter.maybeYouKnowVOs.apply {
                add(MaybeYouKnowVO(1, "Pidor 1", SEEN_TIME_ONLINE, 1, 15))
                add(MaybeYouKnowVO(2, "Pidor 2", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(3, "Pidor 3", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(4, "Pidor 4", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(5, "Pidor 5", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(6, "Pidor 6", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(7, "Pidor 7", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(8, "Pidor 8", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(9, "Pidor 9", SEEN_TIME_ONLINE, 1, 18))
            }
        }

        peopleTabContentList.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }
    }

    override fun getPageTitle(context: Context): CharSequence? {
        return context.getString(R.string.people)
    }

    override fun onPageSelected(activity: CoreActivity) {
        activity.getApplicationBarManager().let {
            it.reset(false)
            it.setListener(this)
            it.toggleIconButtonAtEnd(R.drawable.ic_search)

            adapter!!.toggleLoading(FRIEND_REQUESTS_HEADER_TYPE, true)
            adapter!!.toggleLoading(ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE, true)
        }
    }

    override fun onButtonClicked(tag: Int) {
        adapter!!.toggleLoading(FRIEND_REQUESTS_HEADER_TYPE, false)
        adapter!!.toggleLoading(ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE, false)
    }
}