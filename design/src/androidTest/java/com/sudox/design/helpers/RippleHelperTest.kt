package com.sudox.design.helpers

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.util.StateSet
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sudox.design.R
import com.sudox.design.drawables.ripple.RippleMaskDrawable
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RippleHelperTest : Assert() {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        context.setTheme(R.style.SudoxTheme)
    }

    @Test
    fun testGetRippleColorState() {
        val controlHighlightColor = context.theme.getControlHighlightColor()
        val result = context.getRippleColorState()
        val colorForWildcard = result.getColorForState(StateSet.WILD_CARD, 0)
        assertEquals(controlHighlightColor, colorForWildcard)
    }

    @Test
    fun testAddRipple() {
        val validColorState = context.getRippleColorState()
        val validColors = validColorState.javaClass
                .getDeclaredField("mColors")
                .apply { isAccessible = true }
                .get(validColorState) as IntArray?

        val view = View(context)
        view.addRipple()

        assertTrue(view.background is RippleDrawable)

        val rippleDrawable = view.background as RippleDrawable
        val maskDrawable = rippleDrawable.getDrawable(0)

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
}