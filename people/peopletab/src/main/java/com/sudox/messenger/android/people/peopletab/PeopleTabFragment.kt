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
import com.sudox.messenger.android.people.peopletab.adapters.ADDED_FRIENDS_AND_SUBSCRIPTIONS_TAG
import com.sudox.messenger.android.people.peopletab.adapters.PeopleTabAdapter
import com.sudox.messenger.android.people.peopletab.vos.AddedFriendVO
import com.sudox.messenger.android.people.peopletab.vos.FriendRequestVO
import com.sudox.messenger.android.people.peopletab.vos.MaybeYouKnowVO
import kotlinx.android.synthetic.main.fragment_people_tab.peopleTabContentList

class PeopleTabFragment : CoreFragment(), ViewPagerFragment, ApplicationBarListener {

    private var adapter: PeopleTabAdapter? = null
    private var toggle: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_people_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PeopleTabAdapter().apply {
            viewList = peopleTabContentList

            friendsRequestsVO.apply {
                add(FriendRequestVO(4, "Pidor Request", 145, 2, null, 2L))
                add(FriendRequestVO(5, "Pidor Request", 145, 2, "Hello, Pidor", 1L))
                add(FriendRequestVO(5, "Pidor Request", 145, 2, "Hello, Pidor", 1L))
                add(FriendRequestVO(5, "Pidor Request", 145, 2, "Hello, Pidor", 1L))
                add(FriendRequestVO(5, "Pidor Request", 145, 2, "Hello, Pidor", 1L))
                add(FriendRequestVO(5, "Pidor Request", 145, 2, "Hello, Pidor", 1L))
                add(FriendRequestVO(6, "Pidor Request Last", 146, 2, null, 3L))
            }

            addedFriendsVOs.apply {
                add(AddedFriendVO(1, "Pidor Added", 145, 2, 1))
                add(AddedFriendVO(2, "Pidor Added", 145, 2, 2))
                add(AddedFriendVO(3, "Pidor Added", 145, 2, 3))
                add(AddedFriendVO(4, "Pidor Added", 145, 2, 4))
                add(AddedFriendVO(5, "Pidor Added", 145, 2, 5))
                add(AddedFriendVO(6, "Pidor Added", 145, 2, 6))
            }

            maybeYouKnowAdapter.maybeYouKnowVOs.apply {
                add(MaybeYouKnowVO(1, "Pidor", SEEN_TIME_ONLINE, 1, 15))
                add(MaybeYouKnowVO(2, "Pidor", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(3, "Pidor", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(4, "Pidor", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(5, "Pidor", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(6, "Pidor", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(7, "Pidor", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(8, "Pidor", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(9, "Pidor", SEEN_TIME_ONLINE, 1, 18))
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
        }
    }

    override fun onButtonClicked(tag: Int) {
        toggle = !toggle
        adapter!!.toggleLoadingForHeader(ADDED_FRIENDS_AND_SUBSCRIPTIONS_TAG, toggle)
    }
}