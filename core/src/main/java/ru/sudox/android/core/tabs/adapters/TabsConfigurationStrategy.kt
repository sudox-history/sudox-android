package ru.sudox.android.core.tabs.adapters

import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Стратегия для связи TabLayout'а и ViewPager2.
 * Проставляет заголовки табам.
 *
 * @param adapter Адаптер ViewPager'а
 */
class TabsConfigurationStrategy(
        val adapter: TabsPagerAdapter
) : TabLayoutMediator.TabConfigurationStrategy {

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        tab.text = adapter.getTitle(position)
    }
}