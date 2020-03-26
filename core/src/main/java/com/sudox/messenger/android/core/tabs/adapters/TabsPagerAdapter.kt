package com.sudox.messenger.android.core.tabs.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.tabs.TabsRootFragment

/**
 * Адаптер для ViewPager'а TabsRootFragment
 * Отвечает за инициализацию отображения фрагментов-вкладок и выдачу их названий
 *
 * @param fragments Дочерние фрагменты-вкладки
 * @param rootFragment Основной фрагмент
 */
class TabsPagerAdapter(
        private val fragments: Array<CoreFragment>,
        private val rootFragment: TabsRootFragment
) : FragmentStateAdapter(rootFragment) {

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}