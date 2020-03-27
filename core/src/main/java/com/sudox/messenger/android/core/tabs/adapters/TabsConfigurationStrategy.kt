package com.sudox.messenger.android.core.tabs.adapters

import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TabsConfigurationStrategy(
        val adapter: TabsPagerAdapter
) : TabLayoutMediator.TabConfigurationStrategy {

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        tab.text = adapter.getTitle(position)
    }
}