package ru.sudox.android.core.tabs.callbacks

import androidx.viewpager2.widget.ViewPager2
import ru.sudox.android.core.CoreActivity
import ru.sudox.android.core.CoreFragment
import ru.sudox.android.core.tabs.TabsChildFragment

class TabsRootPageCallback(
        private val activity: CoreActivity,
        private val fragments: Array<CoreFragment>
) : ViewPager2.OnPageChangeCallback() {

    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        // P.S.: В данный адаптер попадают только фрагменты, реализующие интерфейс TabsChildFragment
        (fragments[position] as TabsChildFragment).prepareToShowing(activity, fragments[position])
    }
}