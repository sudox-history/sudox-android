package com.sudox.design.widgets.etlayout

import android.graphics.Canvas
import android.graphics.Paint
import android.widget.EditText
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
@PrepareForTest(EditTextLayoutLabel::class, Canvas::class, EditText::class, Paint::class)
class EditTextLayoutLabelTest : Assert() {

    @Test
    fun testInactiveState() {
        val editTextLayoutLabel = PowerMockito.mock(EditTextLayoutLabel::class.java)
        val editText = PowerMockito.mock(EditText::class.java)
        val params = EditTextLayoutLabelParams()

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
        PowerMockito.`when`(editText.compoundPaddingLeft).thenReturn(10)
        editTextLayoutLabel.dispatchDraw(canvas)

        Mockito.verify(paint).color = 3
        Mockito.verify(canvas).drawText("Test", 10F, 15F, paint)
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
}