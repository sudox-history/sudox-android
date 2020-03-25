package com.sudox.messenger.android.core.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.sudox.design.tablayout.TabLayout
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment

const val VIEW_PAGER_ID_KEY = "view_pager_id"

/**
 * Основной фрагмент-контроллер для фрагментов-вкладок.
 *
 * Отвечает за:
 * 1) Сохранение состояния  ViewPager'а и его инициализацию
 * 2) Инъекцию зависимостей в дочерние фрагменты-вкладки
 */
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

            pagerAdapter = TabsPagerAdapter(activity as CoreActivity, fragments, childFragmentManager)

            pager.adapter = pagerAdapter
            pager.id = savedInstanceState?.getInt(VIEW_PAGER_ID_KEY, View.generateViewId()) ?: View.generateViewId()
            pager.addOnPageChangeListener(pagerAdapter!!)

            tabLayout = TabLayout(context!!).apply {
                syncWithViewPager(pager)
            }
        }

        return viewPager!!
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(VIEW_PAGER_ID_KEY, viewPager!!.id)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            screenManager!!.reset()
            pagerAdapter!!.onPageSelected(viewPager!!.currentItem)
        }
    }

    /**
     * Возвращает массив с дочерними фрагментами-вкладками
     *
     * @return Массив с дочерними фрагментами-вкладками
     */
    abstract fun getFragments(): Array<CoreFragment>
}