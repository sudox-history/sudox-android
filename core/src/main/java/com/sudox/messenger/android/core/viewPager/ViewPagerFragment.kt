package com.sudox.messenger.android.core.viewPager

import android.content.Context
import com.sudox.messenger.android.core.CoreActivity

interface ViewPagerFragment {
    fun getPageTitle(context: Context): CharSequence?
    fun onPageSelected(activity: CoreActivity, position: Int)
}