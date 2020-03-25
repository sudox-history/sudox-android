package com.sudox.messenger.android.core.tabs

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment

/**
 * Адаптер для ViewPager'а TabsRootFragment
 * Отвечает за инициализацию отображения фрагментов-вкладок и выдачу их названий
 *
 * @param activity Основная активность приложения
 * @param fragments Дочерние фрагменты-вкладки
 * @param fragmentManager Менеджер фрагментов
 */
class TabsPagerAdapter(
        private val activity: CoreActivity,
        private val fragments: Array<CoreFragment>,
        fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT), ViewPager.OnPageChangeListener {

    override fun getItem(position: Int): CoreFragment {
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        @Suppress("CAST_NEVER_SUCCEEDS")
        // P.S.: В данный адаптер попадают только фрагменты, реализующие интерфейс TabsChildFragment
        return (fragments[position] as TabsChildFragment).getTitle(activity as Context)
    }

    override fun onPageSelected(position: Int) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        // P.S.: В данный адаптер попадают только фрагменты, реализующие интерфейс TabsChildFragment
        (fragments[position] as TabsChildFragment).prepareToShowing(activity, fragments[position])
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }
}