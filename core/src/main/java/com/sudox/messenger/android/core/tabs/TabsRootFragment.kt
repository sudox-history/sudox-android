package com.sudox.messenger.android.core.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.tabs.adapters.TabsPagerAdapter
import com.sudox.messenger.android.core.tabs.vos.TabsChildAppBarLayoutVO

const val VIEW_PAGER_ID_KEY = "view_pager_id"

/**
 * Основной фрагмент-контроллер для фрагментов-вкладок.
 *
 * Отвечает за:
 * 1) Сохранение состояния  ViewPager'а и его инициализацию
 * 2) Инъекцию зависимостей в дочерние фрагменты-вкладки
 */
abstract class TabsRootFragment : CoreFragment() {

    private var pagerAdapter: TabsPagerAdapter? = null
    private var viewPager: ViewPager2? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewPager = ViewPager2(context!!).also { pager ->
            val fragments = getFragments()

            for (fragment in fragments) {
                fragment.injectAll(activity as CoreActivity)
            }

            pagerAdapter = TabsPagerAdapter(activity as CoreActivity, fragments, this)

            pager.adapter = pagerAdapter
            pager.id = savedInstanceState?.getInt(VIEW_PAGER_ID_KEY, View.generateViewId()) ?: View.generateViewId()
//            pager.addOnPageChangeListener(pagerAdapter!!)

            if (appBarLayoutVO !is TabsChildAppBarLayoutVO) {
                appBarLayoutVO = TabsChildAppBarLayoutVO(appBarLayoutVO)
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

//            (activity as CoreActivity).setAppBarLayoutViewObject(appBarLayoutVO)
//            (appBarLayoutVO as TabsChildAppBarLayoutVO).tabLayout!!.syncWithViewPager(viewPager!!)

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