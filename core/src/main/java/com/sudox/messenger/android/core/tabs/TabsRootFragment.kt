package com.sudox.messenger.android.core.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.sudox.design.tablayout.TabLayout
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment

abstract class TabsRootFragment : CoreFragment() {

    private var tabLayout: TabLayout? = null
    private var pagerAdapter: TabsPagerAdapter? = null
    private var viewPager: ViewPager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewPager = ViewPager(context!!).also { pager ->
            val fragments = getFragments()

            for (fragment in fragments) {
                fragment.injectAll(activity as CoreActivity)
            }

            pager.adapter = TabsPagerAdapter(context!!, fragments, childFragmentManager)
            pager.id = View.generateViewId()

            tabLayout = TabLayout(context!!).apply {
                syncWithViewPager(pager)
            }
        }

        return viewPager!!
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            screenManager!!.reset()
            applicationBarManager!!.let {
                it.reset(true)
                it.setContentView(tabLayout)
            }

            pagerAdapter!!.onPageSelected(viewPager!!.currentItem)
        }
    }

    /**
     * Возвращает массив с дочерними фрагментами-вкладками
     *
     * @return Массив с дочерними фрагментами-вкладками
     */
    abstract fun getFragments(): Array<TabsChildFragment>
}