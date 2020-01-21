package com.sudox.messenger.android.messages

import android.content.Context
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.viewPager.ViewPagerFragment

class RoomsFragment: CoreFragment(), ViewPagerFragment, ApplicationBarListener {
    override fun getPageTitle(context: Context): CharSequence? {
        return context.getString(R.string.rooms)
    }

    override fun onPageSelected(activity: CoreActivity) {
        activity.getApplicationBarManager().let {
            it.reset(false)
            it.setListener(this)
            it.toggleIconButtonAtEnd(R.drawable.ic_search)
        }
    }

    override fun onButtonClicked(tag: Int) {

    }
}