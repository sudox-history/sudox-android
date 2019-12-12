package com.sudox.messenger.android.core.viewPager

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.sudox.messenger.android.core.CoreActivity

class ViewPagerAdapter(
        val context: Context,
        val activity: CoreActivity,
        val viewPager: ViewPager,
        val fragmentManager: FragmentManager,
        val fragments: Array<ViewPagerFragment>
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT), ViewPager.OnPageChangeListener {

    override fun getItem(position: Int): Fragment {
        return fragments[position] as Fragment
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragments[position].getPageTitle(context)
    }

    override fun onPageSelected(position: Int) {
        fragments[position].onPageSelected(activity, position)
    }

    fun selectCurrentFragment() {
        onPageSelected(viewPager.currentItem)
    }

    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
}