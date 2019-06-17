package com.sudox.design.drawables.ripple

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(RippleMaskDrawable::class, Paint::class, Canvas::class, Rect::class)
class RippleMaskDrawableTest : Assert() {

    private lateinit var rippleMaskDrawable: RippleMaskDrawable
    private lateinit var paint: Paint

    @Before
    fun setUp() {
        rippleMaskDrawable = PowerMockito.mock(RippleMaskDrawable::class.java)
        paint = Mockito.mock(Paint::class.java)
    }

    @Test
    fun testGetRadius_bordered() {
        val bounds = PowerMockito.mock(Rect::class.java)

        Mockito.`when`(bounds.width()).thenReturn(15)
        Mockito.`when`(bounds.height()).thenReturn(5)
        Mockito.`when`(rippleMaskDrawable.bounds).thenReturn(bounds)
        Mockito.`when`(rippleMaskDrawable.getRadius()).thenCallRealMethod()

        RippleMaskDrawable::class.java
                .getDeclaredField("type")
                .apply { isAccessible = true }
                .set(rippleMaskDrawable, RippleMaskType.BORDERED)

        val result = rippleMaskDrawable.getRadius()
        val valid = (Math.max(bounds.width(), bounds.height()) / 2).toFloat()
        assertEquals(valid, result)
    }

    @Test
    fun testGetRadius_borderless() {
        val bounds = PowerMockito.mock(Rect::class.java)

        Mockito.`when`(bounds.centerX()).thenReturn(30)
        Mockito.`when`(bounds.centerY()).thenReturn(40)
        Mockito.`when`(rippleMaskDrawable.bounds).thenReturn(bounds)
        Mockito.`when`(rippleMaskDrawable.getRadius()).thenCallRealMethod()

        RippleMaskDrawable::class.java
                .getDeclaredField("type")
                .apply { isAccessible = true }
                .set(rippleMaskDrawable, RippleMaskType.BORDERLESS)

        val result = rippleMaskDrawable.getRadius()
        val valid =  Math.ceil(Math.sqrt((bounds.left - bounds.centerX()) *
                (bounds.left - bounds.centerX()) + (bounds.top - bounds.centerY()) *
                (bounds.top - bounds.centerY()).toDouble())).toFloat()

        assertEquals(valid, result)
    }

    @Test
    fun testGetRadius_specified() {
        val valid = 40F

        RippleMaskDrawable::class.java
                .getDeclaredField("specifiedRadius")
                .apply { isAccessible = true }
                .set(rippleMaskDrawable, valid)

        RippleMaskDrawable::class.java
                .getDeclaredField("type")
                .apply { isAccessible = true }
                .set(rippleMaskDrawable, RippleMaskType.WITH_SPECIFIED_RADIUS)

        Mockito.`when`(rippleMaskDrawable.getRadius()).thenCallRealMethod()
        val result = rippleMaskDrawable.getRadius()
        assertEquals(valid, result)
    }

    @Test
    fun testDraw() {
        val bounds = PowerMockito.mock(Rect::class.java)
        val canvas = PowerMockito.mock(Canvas::class.java)
        val paint = PowerMockito.mock(Paint::class.java)

        RippleMaskDrawable::class.java
                .getDeclaredField("paint")
                .apply { isAccessible = true }
                .set(rippleMaskDrawable, paint)

        Mockito.`when`(bounds.exactCenterX()).thenReturn(30F)
        Mockito.`when`(bounds.exactCenterY()).thenReturn(40F)
        Mockito.`when`(rippleMaskDrawable.bounds).thenReturn(bounds)
        Mockito.`when`(rippleMaskDrawable.getRadius()).thenReturn(15F)
        Mockito.`when`(rippleMaskDrawable.draw(canvas)).thenCallRealMethod()

        rippleMaskDrawable.draw(canvas)
        Mockito.verify(canvas).drawCircle(
                bounds.exactCenterX(),
                bounds.exactCenterY(),
                rippleMaskDrawable.getRadius(),
                paint)
    }
}