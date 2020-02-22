package com.sudox.messenger.android.core.tabs

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager

/**
 * Адаптер для ViewPager'а TabsRootFragment
 * Отвечает за инициализацию отображения фрагментов-вкладок и выдачу их названий
 *
 * @param context Контекст приложения/активности
 * @param fragments Дочерние фрагменты-вкладки
 * @param fragmentManager Менеджер фрагментов
 */
class TabsPagerAdapter(
        private val context: Context,
        private val fragments: Array<TabsChildFragment>,
        fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT), ViewPager.OnPageChangeListener {

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragments[position].getTitle(context)
    }

    override fun onPageSelected(position: Int) {
        fragments[position].prepareToShowing()
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }
}