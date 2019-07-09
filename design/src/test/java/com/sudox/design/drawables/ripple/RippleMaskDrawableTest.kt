package com.sudox.design.drawables.ripple

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import com.sudox.design.DesignTestRunner
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf

@RunWith(DesignTestRunner::class)
class RippleMaskDrawableTest : Assert() {

    @Test
    fun testDrawBorderedRipple_width_equals_height() {
        val canvas = Canvas()

        RippleMaskDrawable(RippleMaskType.BORDERED).apply {
            setBounds(0, 0, 100, 100)
            draw(canvas)
        }

        val shadow = shadowOf(canvas)
        val event = shadow.getDrawnCircle(0)

        assertNotNull(event)
        assertEquals(Color.WHITE, event.paint.color)
        assertEquals(50F, event.radius)
        assertEquals(50F, event.centerX)
        assertEquals(50F, event.centerY)
    }

    @Test
    fun testDrawBorderedRipple_width_more_than_height() {
        val canvas = Canvas()

        RippleMaskDrawable(RippleMaskType.BORDERED).apply {
            setBounds(0, 0, 150, 100)
            draw(canvas)
        }

        val shadow = shadowOf(canvas)
        val event = shadow.getDrawnCircle(0)

        assertNotNull(event)
        assertEquals(Color.WHITE, event.paint.color)
        assertEquals(75F, event.radius)
        assertEquals(75F, event.centerX)
        assertEquals(50F, event.centerY)
    }

    @Test
    fun testDrawBorderlessRipple_width_equals_height() {
        val canvas = Canvas()
        RippleMaskDrawable(RippleMaskType.BORDERLESS).apply {
            setBounds(0, 0, 100, 100)
            draw(canvas)
        }

        val shadow = shadowOf(canvas)
        val event = shadow.getDrawnCircle(0)

        assertNotNull(event)
        assertEquals(Color.WHITE, event.paint.color)
        assertEquals(71F, event.radius)
        assertEquals(50F, event.centerX)
        assertEquals(50F, event.centerY)
    }

    @Test
    fun testDrawBorderlessRipple_width_more_than_height() {
        val canvas = Canvas()

        RippleMaskDrawable(RippleMaskType.BORDERLESS).apply {
            setBounds(0, 0, 150, 100)
            draw(canvas)
        }

        val shadow = shadowOf(canvas)
        val event = shadow.getDrawnCircle(0)

        assertNotNull(event)
        assertEquals(Color.WHITE, event.paint.color)
        assertEquals(107F, event.radius)
        assertEquals(75F, event.centerX)
        assertEquals(50F, event.centerY)
    }
}