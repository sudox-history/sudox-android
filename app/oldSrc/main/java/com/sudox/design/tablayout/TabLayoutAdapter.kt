package com.sudox.design.tablayout

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager

class TabLayoutAdapter(val fragments: Array<TabLayoutFragment>,
                       val titles: Array<String>,
                       viewPager: androidx.viewpager.widget.ViewPager,
                       fragmentManager: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fragmentManager) {

    init {
        viewPager.offscreenPageLimit = fragments.size
        viewPager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(index: Int) {
                fragments[index].onSelected()
            }
        })

        // For first fragment
        viewPager.post { fragments[0].onSelected() }
    }

    override fun getItem(position: Int) = fragments[position]
    override fun getPageTitle(position: Int) = titles[position]
    override fun getCount() = fragments.size
}