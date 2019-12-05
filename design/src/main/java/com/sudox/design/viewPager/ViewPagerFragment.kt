package com.sudox.design.viewPager

interface ViewPagerFragment {
    fun getPageTitle(): CharSequence?
    fun onPageSelected(position: Int)
}