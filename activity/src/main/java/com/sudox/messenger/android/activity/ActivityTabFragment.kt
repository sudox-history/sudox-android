package com.sudox.messenger.android.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.design.circularupdatableview.CircularUpdatableView
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.fragments.ViewListFragment
import com.sudox.messenger.android.core.tabs.TabsChildFragment
import com.sudox.messenger.android.moments.vos.MomentVO
import com.sudox.messenger.android.people.common.vos.SimplePeopleVO

class ActivityTabFragment : ViewListFragment<ViewListAdapter<*>>(), TabsChildFragment, ApplicationBarListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LinearLayout(context).apply {
            addView(CircularUpdatableView(context!!).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                vo = MomentVO(SimplePeopleVO(1L, "kerjen", 1L), 1L, false)
            })
        }
    }

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