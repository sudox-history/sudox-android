package ru.sudox.android.core.ui.toolbar

import android.app.Activity
import android.graphics.drawable.ShapeDrawable
import android.os.Build
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import ru.sudox.android.core.ui.CommonUiRunner

@RunWith(CommonUiRunner::class)
class CenteredToolbarTest : Assert() {

    @Test
    fun checkThatNavigationButtonContainsTooltip() {
        val controller = Robolectric.buildActivity(Activity::class.java).apply {
            create()
            visible()
        }

        val activity = controller.get()
        val toolbar = CenteredToolbar(activity)

        activity.setContentView(toolbar)
        toolbar.navigationIcon = ShapeDrawable()
        toolbar.navigationContentDescription = "Test"

        var tooltipText: Any?

        if (Build.VERSION.SDK_INT >= 26) {
            tooltipText = toolbar.navigationButton!!.tooltipText
        } else {
            val listener = shadowOf(toolbar.navigationButton).onLongClickListener::class.java

            tooltipText = listener
                .getDeclaredField("mTooltipText")
                .apply { isAccessible = true }
                .get(listener)
        }

        assertEquals("Test", tooltipText)
    }
}