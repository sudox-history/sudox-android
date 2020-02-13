package com.sudox.messenger.android.people.peopletab

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.viewPager.ViewPagerFragment
import com.sudox.messenger.android.people.common.vos.SEEN_TIME_ONLINE
import com.sudox.messenger.android.people.peopletab.adapters.ADDED_FRIENDS_HEADER_TAG
import com.sudox.messenger.android.people.peopletab.adapters.MaybeYouKnowAdapter
import com.sudox.messenger.android.people.peopletab.adapters.PeopleTabAdapter
import com.sudox.messenger.android.people.peopletab.vos.MaybeYouKnowVO

class PeopleTabFragment : CoreFragment(), ViewPagerFragment, ApplicationBarListener {

    private var adapter: PeopleTabAdapter? = null
    private var toggle: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return createMaybeYouKnowRecyclerView(context!!).apply {
            (adapter as MaybeYouKnowAdapter).maybeYouKnowVOs.apply {
                add(MaybeYouKnowVO(1, "Pidor", SEEN_TIME_ONLINE, 1, 15))
                add(MaybeYouKnowVO(1, "Pidor", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(1, "Pidor", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(1, "Pidor", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(1, "Pidor", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(1, "Pidor", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(1, "Pidor", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(1, "Pidor", SEEN_TIME_ONLINE, 1, 18))
                add(MaybeYouKnowVO(1, "Pidor", SEEN_TIME_ONLINE, 1, 18))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        adapter = PeopleTabAdapter(peopleTabContentList)
//
//        peopleTabContentList.let {
//            it.layoutManager = LinearLayoutManager(context)
//            it.adapter = adapter
//        }
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
        adapter!!.toggleLoadingForHeader(ADDED_FRIENDS_HEADER_TAG, toggle)
    }
}