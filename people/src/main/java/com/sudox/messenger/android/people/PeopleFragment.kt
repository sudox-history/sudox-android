package com.sudox.messenger.android.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.design.tabLayout.TabLayout
import com.sudox.messenger.android.activity.ActivityFragment
import com.sudox.messenger.android.core.viewPager.ViewPagerAdapter
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.friends.FriendsFragment
import kotlinx.android.synthetic.main.fragment_people.peopleViewPager

class PeopleFragment : CoreFragment() {

    private var tabLayout: TabLayout? = null
    private var pagerAdapter: ViewPagerAdapter? = null
    private var coreActivity: CoreActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coreActivity = activity as CoreActivity
        pagerAdapter = ViewPagerAdapter(context!!, coreActivity!!, peopleViewPager, childFragmentManager, arrayOf(
                ActivityFragment(),
                FriendsFragment()
        ))

        peopleViewPager.adapter = pagerAdapter
        peopleViewPager.addOnPageChangeListener(pagerAdapter!!)

        tabLayout = TabLayout(context!!)
        tabLayout!!.setViewPager(peopleViewPager)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            coreActivity!!.getScreenManager().reset()
            coreActivity!!.getApplicationBarManager().let {
                it.reset(true)
                it.setContentView(tabLayout)
            }

            pagerAdapter!!.restoreCurrentFragment()
        }
    }
}