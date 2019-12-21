package com.sudox.messenger.android.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.viewPager.ViewPagerFragment
import kotlinx.android.synthetic.main.fragment_activity.momentView

class ActivityFragment : CoreFragment(), ViewPagerFragment, ApplicationBarListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        momentView.setUserName("Vlad")
        momentView.setUserPhoto(resources.getDrawable(R.drawable.drawable_photo_3, context!!.theme))
        momentView.setCreatedByMe(true)
        momentView.setViewed(false)
    }

    override fun onButtonClicked(tag: Int) {
    }

    override fun onPageSelected(activity: CoreActivity) {
        activity.getApplicationBarManager().let {
            it.reset(false)
            it.setListener(this)
            it.toggleIconButtonAtStart(R.drawable.ic_notifications_none)
            it.toggleIconButtonAtEnd(R.drawable.ic_search)
        }
    }

    override fun getPageTitle(context: Context): CharSequence? {
        return context.getString(R.string.activity)
    }
}