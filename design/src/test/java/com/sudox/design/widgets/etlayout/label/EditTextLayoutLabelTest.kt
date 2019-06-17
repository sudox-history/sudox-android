package com.sudox.design.widgets.etlayout.label

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.widget.EditText
import com.sudox.design.helpers.isTextRtl
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(EditTextLayoutLabel::class, Canvas::class, EditText::class, Paint::class, Rect::class,
        fullyQualifiedNames = ["com.sudox.design.helpers.TextHelperKt"])
class EditTextLayoutLabelTest : Assert() {

    @Test
    fun testInactiveState() {
        val editTextLayoutLabel = PowerMockito.mock(EditTextLayoutLabel::class.java)
        val editText = PowerMockito.mock(EditText::class.java)
        val params = EditTextLayoutLabelParams()

        PowerMockito.`when`(editTextLayoutLabel.needShowingError()).thenCallRealMethod()

        EditTextLayoutLabel::class.java
                .getDeclaredField("editText")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, editText)

        EditTextLayoutLabel::class.java
                .getDeclaredField("errorText")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, "Error")

        EditTextLayoutLabel::class.java
                .getDeclaredField("originalText")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, "Original")

        EditTextLayoutLabel::class.java
                .getDeclaredField("params")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, params)

        params.errorTextColor = 1

        PowerMockito.`when`(editText.currentHintTextColor).thenReturn(2)
        PowerMockito.`when`(editText.isEnabled).thenReturn(true)
        PowerMockito.`when`(editTextLayoutLabel.isEditTextActive()).thenReturn(false)
        PowerMockito.`when`(editTextLayoutLabel.getCurrentColor()).thenCallRealMethod()
        PowerMockito.`when`(editTextLayoutLabel.getCurrentText()).thenCallRealMethod()

        assertEquals(1, editTextLayoutLabel.getCurrentColor())
        assertEquals("Error", editTextLayoutLabel.getCurrentText())

        PowerMockito.`when`(editText.isEnabled).thenReturn(false)
        assertEquals(2, editTextLayoutLabel.getCurrentColor())
        assertEquals("Original", editTextLayoutLabel.getCurrentText())

        // Without error
        PowerMockito.`when`(editText.isEnabled).thenReturn(true)
        EditTextLayoutLabel::class.java
                .getDeclaredField("errorText")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, null)

        assertEquals(2, editTextLayoutLabel.getCurrentColor())
        assertEquals("Original", editTextLayoutLabel.getCurrentText())

        PowerMockito.`when`(editText.isEnabled).thenReturn(false)
        assertEquals(2, editTextLayoutLabel.getCurrentColor())
        assertEquals("Original", editTextLayoutLabel.getCurrentText())
    }

    @Test
    fun testActiveState() {
        val editTextLayoutLabel = PowerMockito.mock(EditTextLayoutLabel::class.java)
        val editText = PowerMockito.mock(EditText::class.java)
        val params = EditTextLayoutLabelParams()

        PowerMockito.`when`(editTextLayoutLabel.needShowingError()).thenCallRealMethod()

        EditTextLayoutLabel::class.java
                .getDeclaredField("editText")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, editText)

        EditTextLayoutLabel::class.java
                .getDeclaredField("errorText")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, "Error")

        EditTextLayoutLabel::class.java
                .getDeclaredField("originalText")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, "Original")

        EditTextLayoutLabel::class.java
                .getDeclaredField("params")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, params)

        params.errorTextColor = 1

        PowerMockito.`when`(editText.currentHintTextColor).thenReturn(2)
        PowerMockito.`when`(editText.currentTextColor).thenReturn(3)
        PowerMockito.`when`(editText.isEnabled).thenReturn(true)
        PowerMockito.`when`(editTextLayoutLabel.isEditTextActive()).thenReturn(true)
        PowerMockito.`when`(editTextLayoutLabel.getCurrentColor()).thenCallRealMethod()
        PowerMockito.`when`(editTextLayoutLabel.getCurrentText()).thenCallRealMethod()

        assertEquals(1, editTextLayoutLabel.getCurrentColor())
        assertEquals("Error", editTextLayoutLabel.getCurrentText())

        // Without error
        PowerMockito.`when`(editText.isEnabled).thenReturn(true)

        EditTextLayoutLabel::class.java
                .getDeclaredField("errorText")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, null)

        assertEquals(3, editTextLayoutLabel.getCurrentColor())
        assertEquals("Original", editTextLayoutLabel.getCurrentText())
    }

    @Test
    fun testDraw_no_text() {
        val editTextLayoutLabel = PowerMockito.mock(EditTextLayoutLabel::class.java)
        val canvas = PowerMockito.mock(Canvas::class.java)

        PowerMockito.`when`(editTextLayoutLabel.getCurrentText()).thenReturn(null)
        PowerMockito.`when`(editTextLayoutLabel.dispatchDraw(canvas)).thenCallRealMethod()
        editTextLayoutLabel.dispatchDraw(canvas)

        Mockito.verify(canvas, Mockito.never()).drawText(anyString(), anyFloat(), anyFloat(), ArgumentMatchers.any())
    }

    @Test
    fun testDraw_normal() {
        val editTextLayoutLabel = PowerMockito.mock(EditTextLayoutLabel::class.java)
        val editText = PowerMockito.mock(EditText::class.java)
        val canvas = PowerMockito.mock(Canvas::class.java)
        val paint = PowerMockito.mock(Paint::class.java)

        EditTextLayoutLabel::class.java
                .getDeclaredField("editText")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, editText)

        EditTextLayoutLabel::class.java
                .getDeclaredField("paint")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, paint)

        PowerMockito.`when`(editTextLayoutLabel.dispatchDraw(canvas)).thenCallRealMethod()
        PowerMockito.`when`(editTextLayoutLabel.getCurrentText()).thenReturn("Test")
        PowerMockito.`when`(editTextLayoutLabel.getHeight()).thenReturn(15)
        PowerMockito.`when`(editTextLayoutLabel.getCurrentColor()).thenReturn(3)
        PowerMockito.`when`(editTextLayoutLabel.getXCoord("Test")).thenReturn(10)
        editTextLayoutLabel.dispatchDraw(canvas)

        Mockito.verify(paint).color = 3
        Mockito.verify(canvas).drawText("Test", 10F, 15F, paint)
    }

    @Test
    fun testXCoordRtl() {
        val editTextLayoutLabel = PowerMockito.mock(EditTextLayoutLabel::class.java)
        val editText = PowerMockito.mock(EditText::class.java)
        val paint = PowerMockito.mock(Paint::class.java)
        val bounds = PowerMockito.mock(Rect::class.java)
        val editTextWidth = 512
        val paddingStart = 10
        val textWidth = 256
        val text = "لوحة المفاتيح العربية"

        EditTextLayoutLabel::class.java
                .getDeclaredField("editText")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, editText)

        EditTextLayoutLabel::class.java
                .getDeclaredField("paint")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, paint)

        EditTextLayoutLabel::class.java
                .getDeclaredField("bounds")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, bounds)

        PowerMockito.mockStatic(Class.forName("com.sudox.design.helpers.TextHelperKt"))
        PowerMockito.`when`(editText.isTextRtl(text)).thenReturn(true)

        PowerMockito.`when`(editText.measuredWidth).thenReturn(editTextWidth)
        PowerMockito.`when`(editText.compoundPaddingStart).thenReturn(paddingStart)
        PowerMockito.`when`(bounds.width()).thenReturn(textWidth)
        PowerMockito.`when`(editTextLayoutLabel.getXCoord(anyString())).thenCallRealMethod()

        val result = editTextLayoutLabel.getXCoord(text)
        assertEquals(editTextWidth - paddingStart - textWidth, result)

        Mockito.verify(paint).getTextBounds(text, 0, text.length, bounds)
    }

    @Test
    fun testXCoordLtr() {
        val editTextLayoutLabel = PowerMockito.mock(EditTextLayoutLabel::class.java)
        val editText = PowerMockito.mock(EditText::class.java)
        val paint = PowerMockito.mock(Paint::class.java)
        val bounds = PowerMockito.mock(Rect::class.java)
        val editTextWidth = 512
        val paddingStart = 10
        val textWidth = 256
        val text = "Hello World"

        EditTextLayoutLabel::class.java
                .getDeclaredField("editText")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, editText)

        EditTextLayoutLabel::class.java
                .getDeclaredField("paint")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, paint)

        PowerMockito.mockStatic(Class.forName("com.sudox.design.helpers.TextHelperKt"))
        PowerMockito.`when`(editText.isTextRtl(text)).thenReturn(false)

        PowerMockito.`when`(editText.measuredWidth).thenReturn(editTextWidth)
        PowerMockito.`when`(editText.compoundPaddingStart).thenReturn(paddingStart)
        PowerMockito.`when`(bounds.width()).thenReturn(textWidth)
        PowerMockito.`when`(editTextLayoutLabel.getXCoord(anyString())).thenCallRealMethod()

        val result = editTextLayoutLabel.getXCoord(text)
        assertEquals(paddingStart, result)
    }

    @Test
    fun testIsEditTextActive() {
        val editTextLayoutLabel = PowerMockito.mock(EditTextLayoutLabel::class.java)
        val editText = PowerMockito.mock(EditText::class.java)

        EditTextLayoutLabel::class.java
                .getDeclaredField("editText")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, editText)

        PowerMockito.`when`(editTextLayoutLabel.isEditTextActive()).thenCallRealMethod()

        PowerMockito.`when`(editText.isFocused).thenReturn(true)
        PowerMockito.`when`(editText.isEnabled).thenReturn(false)
        assertFalse(editTextLayoutLabel.isEditTextActive())

        PowerMockito.`when`(editText.isFocused).thenReturn(false)
        PowerMockito.`when`(editText.isPressed).thenReturn(true)
        PowerMockito.`when`(editText.isEnabled).thenReturn(false)
        assertFalse(editTextLayoutLabel.isEditTextActive())

        PowerMockito.`when`(editText.isFocused).thenReturn(false)
        PowerMockito.`when`(editText.isPressed).thenReturn(true)
        PowerMockito.`when`(editText.isEnabled).thenReturn(true)
        assertTrue(editTextLayoutLabel.isEditTextActive())

        PowerMockito.`when`(editText.isPressed).thenReturn(false)
        PowerMockito.`when`(editText.isFocused).thenReturn(true)
        PowerMockito.`when`(editText.isEnabled).thenReturn(true)
        assertTrue(editTextLayoutLabel.isEditTextActive())
    }

    @Test
    fun testNeedShowingError() {
        val editTextLayoutLabel = PowerMockito.mock(EditTextLayoutLabel::class.java)
        val editText = PowerMockito.mock(EditText::class.java)

        PowerMockito.`when`(editTextLayoutLabel.needShowingError()).thenCallRealMethod()

        EditTextLayoutLabel::class.java
                .getDeclaredField("editText")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, editText)

        EditTextLayoutLabel::class.java
                .getDeclaredField("errorText")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, "Error")

        PowerMockito.`when`(editText.isEnabled).thenReturn(true)
        assertTrue(editTextLayoutLabel.needShowingError())

        PowerMockito.`when`(editText.isEnabled).thenReturn(false)
        assertFalse(editTextLayoutLabel.needShowingError())

        // Without error
        EditTextLayoutLabel::class.java
                .getDeclaredField("errorText")
                .apply { isAccessible = true }
                .set(editTextLayoutLabel, null)

        PowerMockito.`when`(editText.isEnabled).thenReturn(true)
        assertFalse(editTextLayoutLabel.needShowingError())

        PowerMockito.`when`(editText.isEnabled).thenReturn(false)
        assertFalse(editTextLayoutLabel.needShowingError())
    }
}