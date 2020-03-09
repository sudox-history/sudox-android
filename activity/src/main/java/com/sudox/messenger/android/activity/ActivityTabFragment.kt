package com.sudox.messenger.android.activity

import android.content.Context
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.fragments.ViewListFragment
import com.sudox.messenger.android.core.tabs.TabsChildFragment

class ActivityTabFragment : ViewListFragment<ViewListAdapter<*>>(), TabsChildFragment, ApplicationBarListener {

    override fun getTitle(context: Context): String? {
        return context.getString(R.string.activity)
    }

    override fun prepareToShowing(coreFragment: CoreFragment) {
        super.prepareToShowing(coreFragment)

        applicationBarManager!!.let {
            it.setListener(this)
            it.toggleIconButtonAtStart(R.drawable.ic_notifications_none)
            it.toggleIconButtonAtEnd(R.drawable.ic_search)
        }
    }

    override fun getAdapter(viewList: ViewList): ViewListAdapter<*>? {
        return null
    }

    override fun onButtonClicked(tag: Int) {
    }
}