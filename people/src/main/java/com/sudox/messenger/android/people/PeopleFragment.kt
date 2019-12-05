package com.sudox.messenger.android.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.design.viewPager.ViewPagerAdapter
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.people.activity.ActivityFragment
import com.sudox.messenger.android.people.friends.FriendsFragment
import kotlinx.android.synthetic.main.fragment_people.peopleViewPager

class PeopleFragment : CoreFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ViewPagerAdapter(childFragmentManager, arrayOf(
                ActivityFragment(),
                FriendsFragment()
        ))

        peopleViewPager.adapter = adapter
        peopleViewPager.addOnPageChangeListener(adapter)
        peopleViewPager.post { adapter.onPageSelected(0) }
    }
}