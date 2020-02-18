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

            friendsRequestsVO.apply {
                addAll(*Array(10000) {
                    FriendRequestVO(4, "Pidor Request 1", 145, 2, null, 2L)
                })
            }

            subscriptionsVOs.apply {
                addAll(*Array(10000) {
                    SubscriptionVO(1, "TheMax", SEEN_TIME_ONLINE, 45, "Suka, rabotai", 1, 1)
                })
            }

            addedFriendsVOs.apply {
                addAll(*Array(10000) {
                    AddedFriendVO(1, "Pidor Added 1", 145, 2, 1)
                })
            }

            maybeYouKnowAdapter.maybeYouKnowVOs.apply {
                addAll(*Array(10000) {
                    MaybeYouKnowVO(1, "Pidor 1", SEEN_TIME_ONLINE, 1, 15)
                })
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