package com.sudox.design.widgets.navbar

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import com.nhaarman.mockitokotlin2.eq
import com.sudox.design.widgets.navbar.button.NavigationBarButton
import com.sudox.design.widgets.navbar.button.NavigationBarButtonIconDirection
import com.sudox.design.widgets.navbar.button.NavigationBarButtonParams
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(NavigationBarButton::class, Rect::class)
class NavigationBarButtonUnitTest : Assert() {

    private lateinit var navigationBarButton: NavigationBarButton

    @Before
    fun setUp() {
        navigationBarButton = PowerMockito.mock(NavigationBarButton::class.java)
    }

    @Test
    fun testMeasureComponents_only_icon() {
        val drawableWidth = 20
        val drawable = Mockito.mock(Drawable::class.java)
        val params = NavigationBarButtonParams().apply {
            this.iconTextMargin = 10
            this.rightPadding = 2
            this.leftPadding = 3
        }

        Mockito.`when`(drawable.intrinsicWidth).thenReturn(drawableWidth)
        Mockito.`when`(navigationBarButton.calculateWidth()).thenCallRealMethod()

        NavigationBarButton::class.java
                .getDeclaredField("params")
                .apply { isAccessible = true }
                .set(navigationBarButton, params)

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        val result = navigationBarButton.calculateWidth()
        val valid = drawableWidth + params.rightPadding + params.leftPadding
        assertEquals(valid, result)
    }

    @Test
    fun testMeasureComponents_only_text() {
        val textWidth = 20
        val textBounds = PowerMockito.mock(Rect::class.java)
        val text = "Hello"
        val params = NavigationBarButtonParams().apply {
            this.iconTextMargin = 5
            this.rightPadding = 2
            this.leftPadding = 3
        }

        Mockito.`when`(textBounds.width()).thenReturn(textWidth)
        Mockito.`when`(navigationBarButton.calculateWidth()).thenCallRealMethod()

        NavigationBarButton::class.java
                .getDeclaredField("params")
                .apply { isAccessible = true }
                .set(navigationBarButton, params)

        NavigationBarButton::class.java
                .getDeclaredField("text")
                .apply { isAccessible = true }
                .set(navigationBarButton, text)

        NavigationBarButton::class.java
                .getDeclaredField("textBounds")
                .apply { isAccessible = true }
                .set(navigationBarButton, textBounds)

        val result = navigationBarButton.calculateWidth()
        val valid = textWidth + params.rightPadding + params.leftPadding
        assertEquals(valid, result)
    }

    @Test
    fun testMeasureComponents_all() {
        val text = "Hello"
        val drawableWidth = 512
        val drawable = PowerMockito.mock(Drawable::class.java)
        val textWidth = 512
        val textBounds = PowerMockito.mock(Rect::class.java)
        val params = NavigationBarButtonParams().apply {
            this.iconTextMargin = 5
            this.rightPadding = 2
            this.leftPadding = 3
        }

        NavigationBarButton::class.java
                .getDeclaredField("params")
                .apply { isAccessible = true }
                .set(navigationBarButton, params)

        NavigationBarButton::class.java
                .getDeclaredField("text")
                .apply { isAccessible = true }
                .set(navigationBarButton, text)

        NavigationBarButton::class.java
                .getDeclaredField("textBounds")
                .apply { isAccessible = true }
                .set(navigationBarButton, textBounds)

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        Mockito.`when`(drawable.intrinsicWidth).thenReturn(drawableWidth)
        Mockito.`when`(textBounds.width()).thenReturn(textWidth)
        Mockito.`when`(navigationBarButton.calculateWidth()).thenCallRealMethod()

        val result = navigationBarButton.calculateWidth()
        val valid = drawableWidth + params.iconTextMargin + textWidth + params.leftPadding + params.rightPadding
        assertEquals(valid, result)
    }

    @Test
    fun testDispatchDraw_only_icon() {
        val drawable = Mockito.mock(Drawable::class.java)
        val canvas = Mockito.mock(Canvas::class.java)

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        PowerMockito
                .`when`<Unit>(navigationBarButton, "dispatchDraw", canvas)
                .thenCallRealMethod()

        NavigationBarButton::class.java
                .getDeclaredMethod("dispatchDraw", Canvas::class.java)
                .apply { isAccessible = true }
                .invoke(navigationBarButton, canvas)

        Mockito.verify(navigationBarButton).drawIcon(canvas, false)
        Mockito.verify(navigationBarButton, Mockito.never()).drawText(canvas, false)
    }

    @Test
    fun testDispatchDraw_only_text() {
        val canvas = Mockito.mock(Canvas::class.java)
        val text = "Hello"

        NavigationBarButton::class.java
                .getDeclaredField("text")
                .apply { isAccessible = true }
                .set(navigationBarButton, text)

        PowerMockito
                .`when`<Unit>(navigationBarButton, "dispatchDraw", canvas)
                .thenCallRealMethod()

        NavigationBarButton::class.java
                .getDeclaredMethod("dispatchDraw", Canvas::class.java)
                .apply { isAccessible = true }
                .invoke(navigationBarButton, canvas)

        Mockito.verify(navigationBarButton, Mockito.never()).drawIcon(canvas, false)
        Mockito.verify(navigationBarButton).drawText(canvas, false)
    }

    @Test
    fun testDispatchDraw_all() {
        val drawable = Mockito.mock(Drawable::class.java)
        val canvas = Mockito.mock(Canvas::class.java)
        val text = "Hello"

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        NavigationBarButton::class.java
                .getDeclaredField("text")
                .apply { isAccessible = true }
                .set(navigationBarButton, text)

        PowerMockito
                .`when`<Unit>(navigationBarButton, "dispatchDraw", canvas)
                .thenCallRealMethod()

        NavigationBarButton::class.java
                .getDeclaredMethod("dispatchDraw", Canvas::class.java)
                .apply { isAccessible = true }
                .invoke(navigationBarButton, canvas)

        Mockito.verify(navigationBarButton).drawIcon(canvas, false)
        Mockito.verify(navigationBarButton).drawText(canvas, false)
    }

    @Test
    fun testDrawIcon() {
        val drawable = Mockito.mock(Drawable::class.java)
        val canvas = Mockito.mock(Canvas::class.java)
        val leftBorder = 45
        val bottomBorder = 50

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        Mockito.`when`(navigationBarButton.getIconLeftBorder(eq(canvas), anyBoolean())).thenReturn(leftBorder)
        Mockito.`when`(navigationBarButton.getIconBottomBorder(canvas)).thenReturn(bottomBorder)
        Mockito.`when`(navigationBarButton.drawIcon(eq(canvas), anyBoolean())).thenCallRealMethod()

        navigationBarButton.drawIcon(canvas, false)

        Mockito.verify(canvas).save()
        Mockito.verify(canvas).translate(leftBorder.toFloat(), bottomBorder.toFloat())
        Mockito.verify(drawable).draw(canvas)
        Mockito.verify(canvas).restore()
    }

    @Test
    fun testDrawText() {
        val canvas = Mockito.mock(Canvas::class.java)
        val paint = Mockito.mock(Paint::class.java)
        val leftBorder = 45
        val bottomBorder = 50
        val text = "Hello World!"

        NavigationBarButton::class.java
                .getDeclaredField("text")
                .apply { isAccessible = true }
                .set(navigationBarButton, text)

        NavigationBarButton::class.java
                .getDeclaredField("textPaint")
                .apply { isAccessible = true }
                .set(navigationBarButton, paint)

        Mockito.`when`(navigationBarButton.getTextLeftBorder(anyBoolean())).thenReturn(leftBorder)
        Mockito.`when`(navigationBarButton.getTextBottomBorder(canvas)).thenReturn(bottomBorder)
        Mockito.`when`(navigationBarButton.drawText(eq(canvas), anyBoolean())).thenCallRealMethod()

        navigationBarButton.drawText(canvas, false)

        Mockito.verify(canvas).drawText(text, leftBorder.toFloat(), bottomBorder.toFloat(), paint)
    }

    @Test
    fun testSetIconDrawable() {
        val drawable = Mockito.mock(Drawable::class.java)
        val params = NavigationBarButtonParams().apply { this.iconTintColor = -127 }

        NavigationBarButton::class.java
                .getDeclaredField("params")
                .apply { isAccessible = true }
                .set(navigationBarButton, params)

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        Mockito.`when`(navigationBarButton.setIconDrawable(drawable)).thenCallRealMethod()
        Mockito.`when`(drawable.intrinsicHeight).thenReturn(15)
        Mockito.`when`(drawable.intrinsicWidth).thenReturn(20)

        navigationBarButton.setIconDrawable(drawable)
        Mockito.verify(drawable).setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        Mockito.verify(drawable).setTint(params.iconTintColor)
        Mockito.verify(navigationBarButton).requestLayout()
        Mockito.verify(navigationBarButton).invalidate()

        val saved = NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .get(navigationBarButton) as Drawable?

        assertNotNull(saved)
        assertEquals(drawable, saved)
    }

    @Test
    fun testSetText() {
        val paint = Mockito.mock(Paint::class.java)
        val textBounds = Mockito.mock(Rect::class.java)
        val text = "Hello World!"

        NavigationBarButton::class.java
                .getDeclaredField("textPaint")
                .apply { isAccessible = true }
                .set(navigationBarButton, paint)

        NavigationBarButton::class.java
                .getDeclaredField("textBounds")
                .apply { isAccessible = true }
                .set(navigationBarButton, textBounds)

        Mockito.`when`(navigationBarButton.setText(anyString(), anyBoolean())).thenCallRealMethod()

        navigationBarButton.setText(text)

        Mockito.verify(paint).getTextBounds(text, 0, text.length, textBounds)
        Mockito.verify(navigationBarButton).requestLayout()
        Mockito.verify(navigationBarButton).invalidate()

        val saved = NavigationBarButton::class.java
                .getDeclaredField("text")
                .apply { isAccessible = true }
                .get(navigationBarButton) as String?

        assertEquals(text, saved)
    }

    @Test
    fun testIconBottomBorder() {
        val canvas = Mockito.mock(Canvas::class.java)
        val drawable = Mockito.mock(Drawable::class.java)
        val iconHeight = 1000
        val height = 2000

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        Mockito.`when`(navigationBarButton.getIconBottomBorder(canvas)).thenCallRealMethod()
        Mockito.`when`(drawable.intrinsicHeight).thenReturn(iconHeight)
        Mockito.`when`(canvas.height).thenReturn(height)

        val result = navigationBarButton.getIconBottomBorder(canvas)
        assertEquals(height / 2 - iconHeight / 2, result)
    }

    @Test
    fun testIconLeftBorder_without_text_need_draw_on_right() {
        val canvas = Mockito.mock(Canvas::class.java)
        val drawable = Mockito.mock(Drawable::class.java)
        val params = NavigationBarButtonParams().apply {
            this.leftPadding = 50
            this.rightPadding = 20
            this.iconTextMargin = 24
        }

        NavigationBarButton::class.java
                .getDeclaredField("params")
                .apply { isAccessible = true }
                .set(navigationBarButton, params)

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        Mockito.`when`(navigationBarButton.getIconLeftBorder(canvas, false)).thenCallRealMethod()
        Mockito.`when`(navigationBarButton.isIconNeedShowOnRight(false)).thenReturn(true)
        Mockito.`when`(drawable.intrinsicWidth).thenReturn(24)
        Mockito.`when`(canvas.width).thenReturn(50)

        val result = navigationBarButton.getIconLeftBorder(canvas, false)
        assertEquals(params.leftPadding, result)
    }

    @Test
    fun testIconLeftBorder_need_draw_on_right_with_text() {
        val canvas = Mockito.mock(Canvas::class.java)
        val drawable = Mockito.mock(Drawable::class.java)
        val params = NavigationBarButtonParams().apply {
            this.leftPadding = 50
            this.rightPadding = 20
            this.iconTextMargin = 24
        }

        NavigationBarButton::class.java
                .getDeclaredField("params")
                .apply { isAccessible = true }
                .set(navigationBarButton, params)

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        NavigationBarButton::class.java
                .getDeclaredField("text")
                .apply { isAccessible = true }
                .set(navigationBarButton, "Text")

        Mockito.`when`(navigationBarButton.getIconLeftBorder(canvas, false)).thenCallRealMethod()
        Mockito.`when`(navigationBarButton.isIconNeedShowOnRight(false)).thenReturn(true)
        Mockito.`when`(drawable.intrinsicWidth).thenReturn(24)
        Mockito.`when`(canvas.width).thenReturn(50)

        val result = navigationBarButton.getIconLeftBorder(canvas, false)
        val valid = canvas.width - params.rightPadding - drawable.intrinsicWidth
        assertEquals(valid, result)
    }

    @Test
    fun testIconLeftBorder_not_need_draw_on_right_without_text() {
        val canvas = Mockito.mock(Canvas::class.java)
        val drawable = Mockito.mock(Drawable::class.java)
        val params = NavigationBarButtonParams().apply {
            this.leftPadding = 50
            this.rightPadding = 20
            this.iconTextMargin = 24
        }

        NavigationBarButton::class.java
                .getDeclaredField("params")
                .apply { isAccessible = true }
                .set(navigationBarButton, params)

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        Mockito.`when`(navigationBarButton.getIconLeftBorder(canvas, false)).thenCallRealMethod()
        Mockito.`when`(drawable.intrinsicWidth).thenReturn(24)
        Mockito.`when`(canvas.width).thenReturn(50)

        val result = navigationBarButton.getIconLeftBorder(canvas, false)
        assertEquals(params.leftPadding, result)
    }

    @Test
    fun testIconLeftBorder_not_need_draw_on_right_with_text() {
        val canvas = Mockito.mock(Canvas::class.java)
        val drawable = Mockito.mock(Drawable::class.java)
        val params = NavigationBarButtonParams().apply {
            this.leftPadding = 50
            this.rightPadding = 20
            this.iconTextMargin = 24
        }

        NavigationBarButton::class.java
                .getDeclaredField("params")
                .apply { isAccessible = true }
                .set(navigationBarButton, params)

        NavigationBarButton::class.java
                .getDeclaredField("text")
                .apply { isAccessible = true }
                .set(navigationBarButton, "Text")

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        Mockito.`when`(navigationBarButton.getIconLeftBorder(canvas, false)).thenCallRealMethod()
        Mockito.`when`(drawable.intrinsicWidth).thenReturn(24)
        Mockito.`when`(canvas.width).thenReturn(50)

        val result = navigationBarButton.getIconLeftBorder(canvas, false)
        assertEquals(params.leftPadding, result)
    }

    @Test
    fun testTextRightBorder_icon_need_draw_on_right_without_icon() {
        val params = NavigationBarButtonParams().apply {
            this.leftPadding = 50
            this.rightPadding = 20
            this.iconTextMargin = 24
        }

        NavigationBarButton::class.java
                .getDeclaredField("params")
                .apply { isAccessible = true }
                .set(navigationBarButton, params)

        NavigationBarButton::class.java
                .getDeclaredField("iconDirection")
                .apply { isAccessible = true }
                .set(navigationBarButton, NavigationBarButtonIconDirection.END)

        Mockito.`when`(navigationBarButton.isIconNeedShowOnRight(false)).thenReturn(false)
        Mockito.`when`(navigationBarButton.getTextLeftBorder(false)).thenCallRealMethod()

        val result = navigationBarButton.getTextLeftBorder(false)
        assertEquals(params.leftPadding, result)
    }

    @Test
    fun testTextLeftBorder_icon_not_need_draw_on_right_with_icon() {
        val drawable = Mockito.mock(Drawable::class.java)
        val params = NavigationBarButtonParams().apply {
            this.leftPadding = 50
            this.rightPadding = 20
            this.iconTextMargin = 24
        }

        NavigationBarButton::class.java
                .getDeclaredField("params")
                .apply { isAccessible = true }
                .set(navigationBarButton, params)

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        Mockito.`when`(drawable.intrinsicWidth).thenReturn(24)
        Mockito.`when`(navigationBarButton.isIconNeedShowOnRight(false)).thenReturn(false)
        Mockito.`when`(navigationBarButton.getTextLeftBorder(false)).thenCallRealMethod()

        val result = navigationBarButton.getTextLeftBorder(false)
        val valid = params.leftPadding + drawable.intrinsicWidth + params.iconTextMargin
        assertEquals(valid, result)
    }

    @Test
    fun testTextLeftBorder_icon_not_need_draw_on_right_without_icon() {
        val params = NavigationBarButtonParams().apply {
            this.leftPadding = 50
            this.rightPadding = 20
            this.iconTextMargin = 24
        }

        NavigationBarButton::class.java
                .getDeclaredField("params")
                .apply { isAccessible = true }
                .set(navigationBarButton, params)

        Mockito.`when`(navigationBarButton.isIconNeedShowOnRight(false)).thenReturn(false)
        Mockito.`when`(navigationBarButton.getTextLeftBorder(false)).thenCallRealMethod()

        val result = navigationBarButton.getTextLeftBorder(false)
        assertEquals(params.leftPadding, result)
    }

    @Test
    fun testTextLeftBorder_icon_need_draw_on_right_with_icon() {
        val drawable = Mockito.mock(Drawable::class.java)
        val params = NavigationBarButtonParams().apply {
            this.leftPadding = 50
            this.rightPadding = 20
            this.iconTextMargin = 24
        }

        NavigationBarButton::class.java
                .getDeclaredField("params")
                .apply { isAccessible = true }
                .set(navigationBarButton, params)

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        Mockito.`when`(drawable.intrinsicWidth).thenReturn(24)
        Mockito.`when`(navigationBarButton.isIconNeedShowOnRight(false)).thenReturn(true)
        Mockito.`when`(navigationBarButton.getTextLeftBorder(false)).thenCallRealMethod()

        val result = navigationBarButton.getTextLeftBorder(false)
        assertEquals(params.leftPadding, result)
    }

    @Test
    fun testTextBottomBorder() {
        val canvas = PowerMockito.mock(Canvas::class.java)
        val bounds = PowerMockito.mock(Rect::class.java)

        NavigationBarButton::class.java
                .getDeclaredField("textBounds")
                .apply { isAccessible = true }
                .set(navigationBarButton, bounds)

        Mockito.`when`(navigationBarButton.getTextBottomBorder(canvas)).thenCallRealMethod()
        Mockito.`when`(bounds.centerY()).thenReturn(20)
        Mockito.`when`(canvas.height).thenReturn(80)

        val result = navigationBarButton.getTextBottomBorder(canvas)
        val valid = 80 / 2 - bounds.centerY()
        assertEquals(valid, result)
    }

    @Test
    fun testIconNeedShowOnRight_ltr_start() {
        NavigationBarButton::class.java
                .getDeclaredField("iconDirection")
                .apply { isAccessible = true }
                .set(navigationBarButton, NavigationBarButtonIconDirection.START)

        Mockito.`when`(navigationBarButton.isIconNeedShowOnRight(anyBoolean())).thenCallRealMethod()
        assertFalse(navigationBarButton.isIconNeedShowOnRight(false))
    }

    @Test
    fun testIconNeedShowOnRight_ltr_end() {
        NavigationBarButton::class.java
                .getDeclaredField("iconDirection")
                .apply { isAccessible = true }
                .set(navigationBarButton, NavigationBarButtonIconDirection.END)

        Mockito.`when`(navigationBarButton.isIconNeedShowOnRight(anyBoolean())).thenCallRealMethod()
        assertTrue(navigationBarButton.isIconNeedShowOnRight(false))
    }

    @Test
    fun testIconNeedShowOnRight_rtl_start() {
        NavigationBarButton::class.java
                .getDeclaredField("iconDirection")
                .apply { isAccessible = true }
                .set(navigationBarButton, NavigationBarButtonIconDirection.START)

        Mockito.`when`(navigationBarButton.isIconNeedShowOnRight(anyBoolean())).thenCallRealMethod()
        assertTrue(navigationBarButton.isIconNeedShowOnRight(true))
    }

    @Test
    fun testIconNeedShowOnRight_rtl_end() {
        NavigationBarButton::class.java
                .getDeclaredField("iconDirection")
                .apply { isAccessible = true }
                .set(navigationBarButton, NavigationBarButtonIconDirection.END)

        Mockito.`when`(navigationBarButton.isIconNeedShowOnRight(anyBoolean())).thenCallRealMethod()
        assertFalse(navigationBarButton.isIconNeedShowOnRight(true))
    }
}