package ru.sudox.android.core.ui.tablayout

import android.app.Activity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import ru.sudox.android.core.ui.CommonUiRunner

@RunWith(CommonUiRunner::class)
class FixedTabLayoutTest {

    @Test
    fun testThatHeightFixed() {
        val controller = Robolectric.buildActivity(Activity::class.java).apply {
            create()
            visible()
        }

        val activity = controller.get()
        val tabLayout = FixedTabLayout(activity)

        tabLayout.minimumHeight = 1000
        activity.setContentView(tabLayout)

        assertEquals(1000, tabLayout.measuredHeight)
    }
}