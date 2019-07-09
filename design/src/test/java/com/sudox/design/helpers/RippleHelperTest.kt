package com.sudox.design.helpers

import android.app.Activity
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.RippleDrawable
import android.util.StateSet
import android.view.View
import com.sudox.design.DesignTestRunner
import com.sudox.design.drawables.ripple.RippleMaskDrawable
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

@RunWith(DesignTestRunner::class)
class RippleHelperTest : Assert() {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity
    private lateinit var view: View

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()
        view = View(activity)
    }

    @Test
    fun testRippleAdding() {
        view.addRipple()
        assertTrue(view.background is RippleDrawable)

        val rippleDrawable = view.background as RippleDrawable
        val maskDrawable = rippleDrawable.getDrawable(0)
        val (validColorState, validColors) = getValidColors(view.context.theme)

        assertTrue(maskDrawable is RippleMaskDrawable)

        val stateObject = RippleDrawable::class.java
                .getDeclaredField("mState")
                .apply { isAccessible = true }
                .get(rippleDrawable)

        val colorState = stateObject.javaClass
                .getDeclaredField("mColor")
                .apply { isAccessible = true }
                .get(stateObject) as ColorStateList

        val colors = colorState.javaClass
                .getDeclaredField("mColors")
                .apply { isAccessible = true }
                .get(validColorState) as IntArray?

        assertArrayEquals(validColors, colors)
    }

    private fun getValidColors(theme: Resources.Theme): Pair<ColorStateList, IntArray> {
        val color = theme.getControlHighlightColor()
        val states = arrayOf(StateSet.WILD_CARD)
        val colors = intArrayOf(color)

        return Pair(ColorStateList(states, colors), colors)
    }
}