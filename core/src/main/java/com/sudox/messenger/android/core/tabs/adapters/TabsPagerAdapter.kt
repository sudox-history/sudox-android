package com.sudox.messenger.android.core.tabs.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.tabs.TabsChildFragment
import com.sudox.messenger.android.core.tabs.TabsRootFragment

/**
 * Адаптер для ViewPager'а TabsRootFragment
 * Отвечает за инициализацию отображения фрагментов-вкладок и выдачу их названий
 *
 * @param activity Основная активность приложения
 * @param fragments Дочерние фрагменты-вкладки
 * @param rootFragment Основной фрагмент
 */
class TabsPagerAdapter(
        private val activity: CoreActivity,
        private val fragments: Array<CoreFragment>,
        private val rootFragment: TabsRootFragment
) : FragmentStateAdapter(rootFragment), ViewPager.OnPageChangeListener {

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

//    override fun getItem(position: Int): CoreFragment {
//        return fragments[position]
//    }
//
//    override fun getPageTitle(position: Int): CharSequence? {
//        @Suppress("CAST_NEVER_SUCCEEDS")
//        // P.S.: В данный адаптер попадают только фрагменты, реализующие интерфейс TabsChildFragment
//        return (fragments[position] as TabsChildFragment).getTitle(activity as Context)
//    }
//
//    override fun onPageSelected(position: Int) {

//    }
//
//    override fun getCount(): Int {
//        return fragments.size
//    }

    override fun onPageSelected(position: Int) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        // P.S.: В данный адаптер попадают только фрагменты, реализующие интерфейс TabsChildFragment
        (fragments[position] as TabsChildFragment).prepareToShowing(activity, fragments[position])
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }
}