package com.sudox.design.widgets.navbar

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(NavigationBarButton::class)
class NavigationBarButtonUnitTest : Assert() {

    private lateinit var navigationBarButton: NavigationBarButton

    @Before
    fun setUp() {
        navigationBarButton = PowerMockito.mock(NavigationBarButton::class.java)
    }

    @Test
    fun testMeasureComponents_only_icon() {
        val iconTextMargin = 5
        val drawable = Mockito.mock(Drawable::class.java)
        val drawableWidth = 512
        val leftPadding = 2
        val rightPadding = 3

        NavigationBarButton::class.java
                .getDeclaredField("iconTextMargin")
                .apply { isAccessible = true }
                .set(navigationBarButton, iconTextMargin)

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        Mockito.`when`(drawable.intrinsicWidth).thenReturn(drawableWidth)
        Mockito.`when`(navigationBarButton.calculateWidth()).thenCallRealMethod()
        Mockito.`when`(navigationBarButton.paddingLeft).thenReturn(leftPadding)
        Mockito.`when`(navigationBarButton.paddingRight).thenReturn(rightPadding)

        val result = navigationBarButton.calculateWidth()
        assertEquals(drawableWidth + leftPadding + rightPadding, result)
    }

    @Test
    fun testMeasureComponents_only_text() {
        val iconTextMargin = 5
        val textBounds = Mockito.mock(Rect::class.java)
        val textWidth = 512
        val text = "Hello"
        val leftPadding = 2
        val rightPadding = 3

        NavigationBarButton::class.java
                .getDeclaredField("iconTextMargin")
                .apply { isAccessible = true }
                .set(navigationBarButton, iconTextMargin)

        NavigationBarButton::class.java
                .getDeclaredField("text")
                .apply { isAccessible = true }
                .set(navigationBarButton, text)

        NavigationBarButton::class.java
                .getDeclaredField("textBounds")
                .apply { isAccessible = true }
                .set(navigationBarButton, textBounds)

        Mockito.`when`(textBounds.width()).thenReturn(textWidth)
        Mockito.`when`(navigationBarButton.calculateWidth()).thenCallRealMethod()
        Mockito.`when`(navigationBarButton.paddingLeft).thenReturn(leftPadding)
        Mockito.`when`(navigationBarButton.paddingRight).thenReturn(rightPadding)

        val result = navigationBarButton.calculateWidth()
        assertEquals(textWidth + leftPadding + rightPadding, result)
    }

    @Test
    fun testMeasureComponents_all() {
        val iconTextMargin = 5
        val drawable = Mockito.mock(Drawable::class.java)
        val drawableWidth = 512
        val textBounds = Mockito.mock(Rect::class.java)
        val textWidth = 512
        val text = "Hello"
        val leftPadding = 2
        val rightPadding = 3

        NavigationBarButton::class.java
                .getDeclaredField("text")
                .apply { isAccessible = true }
                .set(navigationBarButton, text)

        NavigationBarButton::class.java
                .getDeclaredField("iconTextMargin")
                .apply { isAccessible = true }
                .set(navigationBarButton, iconTextMargin)

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
        Mockito.`when`(navigationBarButton.paddingLeft).thenReturn(leftPadding)
        Mockito.`when`(navigationBarButton.paddingRight).thenReturn(rightPadding)

        val result = navigationBarButton.calculateWidth()
        assertEquals(drawableWidth + iconTextMargin + textWidth + leftPadding + rightPadding, result)
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

        Mockito.verify(navigationBarButton).drawIcon(canvas)
        Mockito.verify(navigationBarButton, Mockito.never()).drawText(canvas)
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

        Mockito.verify(navigationBarButton, Mockito.never()).drawIcon(canvas)
        Mockito.verify(navigationBarButton).drawText(canvas)
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

        Mockito.verify(navigationBarButton).drawIcon(canvas)
        Mockito.verify(navigationBarButton).drawText(canvas)
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

        Mockito.`when`(navigationBarButton.getIconLeftBorder(canvas)).thenReturn(leftBorder)
        Mockito.`when`(navigationBarButton.getIconBottomBorder(canvas)).thenReturn(bottomBorder)
        Mockito.`when`(navigationBarButton.drawIcon(canvas)).thenCallRealMethod()

        navigationBarButton.drawIcon(canvas)

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

        Mockito.`when`(navigationBarButton.getTextLeftBorder()).thenReturn(leftBorder)
        Mockito.`when`(navigationBarButton.getTextBottomBorder(canvas)).thenReturn(bottomBorder)
        Mockito.`when`(navigationBarButton.drawText(canvas)).thenCallRealMethod()

        navigationBarButton.drawText(canvas)

        Mockito.verify(canvas).drawText(text, leftBorder.toFloat(), bottomBorder.toFloat(), paint)
    }

    @Test
    fun testSetIconDrawable() {
        val drawable = Mockito.mock(Drawable::class.java)
        val tintColor = -127

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        NavigationBarButton::class.java
                .getDeclaredField("iconTintColor")
                .apply { isAccessible = true }
                .set(navigationBarButton, tintColor)

        Mockito.`when`(navigationBarButton.setIconDrawable(drawable)).thenCallRealMethod()
        Mockito.`when`(drawable.intrinsicHeight).thenReturn(15)
        Mockito.`when`(drawable.intrinsicWidth).thenReturn(20)

        navigationBarButton.setIconDrawable(drawable)
        Mockito.verify(drawable).setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        Mockito.verify(drawable).setTint(tintColor)
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

        Mockito.`when`(navigationBarButton.setText(anyString())).thenCallRealMethod()

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
    fun testIconLeftBorder_left_icon_direction_without_text() {
        val canvas = Mockito.mock(Canvas::class.java)
        val drawable = Mockito.mock(Drawable::class.java)
        val iconWidth = 1000
        val width = 2000
        val paddingRight = 50
        val paddingLeft = 20

        NavigationBarButton::class.java
                .getDeclaredField("iconDirection")
                .apply { isAccessible = true }
                .set(navigationBarButton, NavigationBarButton.IconDirection.LEFT)

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        Mockito.`when`(navigationBarButton.getIconLeftBorder(canvas)).thenCallRealMethod()
        Mockito.`when`(navigationBarButton.paddingRight).thenReturn(paddingRight)
        Mockito.`when`(navigationBarButton.paddingLeft).thenReturn(paddingLeft)
        Mockito.`when`(drawable.intrinsicWidth).thenReturn(iconWidth)
        Mockito.`when`(canvas.width).thenReturn(width)

        val result = navigationBarButton.getIconLeftBorder(canvas)
        assertEquals(paddingLeft, result)
    }

    @Test
    fun testIconLeftBorder_left_icon_direction_with_text() {
        val canvas = Mockito.mock(Canvas::class.java)
        val drawable = Mockito.mock(Drawable::class.java)
        val iconWidth = 1000
        val width = 2000
        val paddingRight = 50
        val paddingLeft = 20

        NavigationBarButton::class.java
                .getDeclaredField("iconDirection")
                .apply { isAccessible = true }
                .set(navigationBarButton, NavigationBarButton.IconDirection.LEFT)

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        NavigationBarButton::class.java
                .getDeclaredField("text")
                .apply { isAccessible = true }
                .set(navigationBarButton, "Text")

        Mockito.`when`(navigationBarButton.getIconLeftBorder(canvas)).thenCallRealMethod()
        Mockito.`when`(navigationBarButton.paddingRight).thenReturn(paddingRight)
        Mockito.`when`(navigationBarButton.paddingLeft).thenReturn(paddingLeft)
        Mockito.`when`(drawable.intrinsicWidth).thenReturn(iconWidth)
        Mockito.`when`(canvas.width).thenReturn(width)

        val result = navigationBarButton.getIconLeftBorder(canvas)
        assertEquals(paddingLeft, result)
    }

    @Test
    fun testIconLeftBorder_right_icon_direction_without_text() {
        val canvas = Mockito.mock(Canvas::class.java)
        val drawable = Mockito.mock(Drawable::class.java)
        val iconWidth = 1000
        val width = 2000
        val paddingRight = 50
        val paddingLeft = 20

        NavigationBarButton::class.java
                .getDeclaredField("iconDirection")
                .apply { isAccessible = true }
                .set(navigationBarButton, NavigationBarButton.IconDirection.RIGHT)

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        Mockito.`when`(navigationBarButton.getIconLeftBorder(canvas)).thenCallRealMethod()
        Mockito.`when`(navigationBarButton.paddingRight).thenReturn(paddingRight)
        Mockito.`when`(navigationBarButton.paddingLeft).thenReturn(paddingLeft)
        Mockito.`when`(drawable.intrinsicWidth).thenReturn(iconWidth)
        Mockito.`when`(canvas.width).thenReturn(width)

        val result = navigationBarButton.getIconLeftBorder(canvas)
        assertEquals(paddingLeft, result)
    }

    @Test
    fun testIconLeftBorder_right_icon_direction_with_text() {
        val canvas = Mockito.mock(Canvas::class.java)
        val drawable = Mockito.mock(Drawable::class.java)
        val iconWidth = 1000
        val width = 2000
        val paddingRight = 50
        val paddingLeft = 20

        NavigationBarButton::class.java
                .getDeclaredField("iconDirection")
                .apply { isAccessible = true }
                .set(navigationBarButton, NavigationBarButton.IconDirection.RIGHT)

        NavigationBarButton::class.java
                .getDeclaredField("text")
                .apply { isAccessible = true }
                .set(navigationBarButton, "Text")

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        Mockito.`when`(navigationBarButton.getIconLeftBorder(canvas)).thenCallRealMethod()
        Mockito.`when`(navigationBarButton.paddingRight).thenReturn(paddingRight)
        Mockito.`when`(navigationBarButton.paddingLeft).thenReturn(paddingLeft)
        Mockito.`when`(drawable.intrinsicWidth).thenReturn(iconWidth)
        Mockito.`when`(canvas.width).thenReturn(width)

        val result = navigationBarButton.getIconLeftBorder(canvas)
        assertEquals(width - paddingRight - iconWidth, result)
    }

    @Test
    fun testTextRightBorder_left_icon_direction_without_icon() {
        val paddingLeft = 15

        NavigationBarButton::class.java
                .getDeclaredField("iconDirection")
                .apply { isAccessible = true }
                .set(navigationBarButton, NavigationBarButton.IconDirection.RIGHT)

        Mockito.`when`(navigationBarButton.paddingLeft).thenReturn(paddingLeft)
        Mockito.`when`(navigationBarButton.getTextLeftBorder()).thenCallRealMethod()

        val result = navigationBarButton.getTextLeftBorder()
        assertEquals(paddingLeft, result)
    }

    @Test
    fun testTextRightBorder_left_icon_direction_with_icon() {
        val drawable = Mockito.mock(Drawable::class.java)
        val paddingLeft = 15
        val iconTextMargin = 10
        val iconWidth = 512

        NavigationBarButton::class.java
                .getDeclaredField("iconTextMargin")
                .apply { isAccessible = true }
                .set(navigationBarButton, iconTextMargin)

        NavigationBarButton::class.java
                .getDeclaredField("iconDirection")
                .apply { isAccessible = true }
                .set(navigationBarButton, NavigationBarButton.IconDirection.RIGHT)

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        Mockito.`when`(navigationBarButton.paddingLeft).thenReturn(paddingLeft)
        Mockito.`when`(drawable.intrinsicWidth).thenReturn(iconWidth)
        Mockito.`when`(navigationBarButton.getTextLeftBorder()).thenCallRealMethod()

        val result = navigationBarButton.getTextLeftBorder()
        assertEquals(paddingLeft, result)
    }

    @Test
    fun testTextLeftBorder_left_icon_direction_without_icon() {
        val paddingLeft = 15
        val iconTextMargin = 10

        NavigationBarButton::class.java
                .getDeclaredField("iconTextMargin")
                .apply { isAccessible = true }
                .set(navigationBarButton, iconTextMargin)

        NavigationBarButton::class.java
                .getDeclaredField("iconDirection")
                .apply { isAccessible = true }
                .set(navigationBarButton, NavigationBarButton.IconDirection.LEFT)

        Mockito.`when`(navigationBarButton.paddingLeft).thenReturn(paddingLeft)
        Mockito.`when`(navigationBarButton.getTextLeftBorder()).thenCallRealMethod()

        val result = navigationBarButton.getTextLeftBorder()
        assertEquals(paddingLeft, result)
    }

    @Test
    fun testTextLeftBorder_left_icon_direction_with_icon() {
        val drawable = Mockito.mock(Drawable::class.java)
        val paddingLeft = 15
        val iconTextMargin = 10
        val iconWidth = 512

        NavigationBarButton::class.java
                .getDeclaredField("iconTextMargin")
                .apply { isAccessible = true }
                .set(navigationBarButton, iconTextMargin)

        NavigationBarButton::class.java
                .getDeclaredField("iconDirection")
                .apply { isAccessible = true }
                .set(navigationBarButton, NavigationBarButton.IconDirection.LEFT)

        NavigationBarButton::class.java
                .getDeclaredField("iconDrawable")
                .apply { isAccessible = true }
                .set(navigationBarButton, drawable)

        Mockito.`when`(navigationBarButton.paddingLeft).thenReturn(paddingLeft)
        Mockito.`when`(drawable.intrinsicWidth).thenReturn(iconWidth)
        Mockito.`when`(navigationBarButton.getTextLeftBorder()).thenCallRealMethod()

        val result = navigationBarButton.getTextLeftBorder()
        assertEquals(paddingLeft + iconWidth + iconTextMargin, result)
    }

    @Test
    fun testTextBottomBorder() {
        val canvas = Mockito.mock(Canvas::class.java)
        val bounds = Mockito.mock(Rect::class.java)
        val height = 2000
        val textHeight = 1000

        NavigationBarButton::class.java
                .getDeclaredField("textBounds")
                .apply { isAccessible = true }
                .set(navigationBarButton, bounds)

        Mockito.`when`(navigationBarButton.getTextBottomBorder(canvas)).thenCallRealMethod()
        Mockito.`when`(bounds.centerY()).thenReturn(textHeight / 2)
        Mockito.`when`(canvas.height).thenReturn(height)

        val result = navigationBarButton.getTextBottomBorder(canvas)
        assertEquals(height / 2 - textHeight / 2, result)
    }
}