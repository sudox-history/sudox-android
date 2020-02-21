package com.sudox.messenger.android.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.design.tablayout.TabLayout
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.viewPager.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_dialogs.*

class DialogsFragment : CoreFragment() {

    private var tabLayout: TabLayout? = null
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private var coreActivity: CoreActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_dialogs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coreActivity = activity as CoreActivity
        viewPagerAdapter = ViewPagerAdapter(context!!, coreActivity!!, dialogsViewPager, childFragmentManager, arrayOf(
                MessagesFragment(),
                TalksFragment()
        ))

        dialogsViewPager.adapter = viewPagerAdapter
        dialogsViewPager.addOnPageChangeListener(viewPagerAdapter!!)

        tabLayout = TabLayout(context!!).apply {
            syncWithViewPager(dialogsViewPager)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            coreActivity!!.getScreenManager().reset()
            coreActivity!!.getApplicationBarManager().let {
                it.reset(true)
                it.setContentView(tabLayout)
            }

            viewPagerAdapter!!.restoreCurrentFragment()
        }
    }
}