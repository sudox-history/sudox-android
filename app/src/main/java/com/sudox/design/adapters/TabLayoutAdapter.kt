package com.sudox.design.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager

class TabLayoutAdapter(val fragments: Array<Fragment>,
                       val titles: Array<String>,
                       viewPager: ViewPager,
                       fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    init {
        viewPager.offscreenPageLimit = fragments.size
    }

    override fun getItem(position: Int) = fragments[position]
    override fun getPageTitle(position: Int) = titles[position]
    override fun getCount() = fragments.size
}