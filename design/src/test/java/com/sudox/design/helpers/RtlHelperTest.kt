package com.sudox.design.helpers

import android.view.View
import androidx.core.text.TextDirectionHeuristicsCompat
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class RtlHelperTest : Assert() {

    private lateinit var view: View

    @Before
    fun setUp() {
        view = Mockito.mock(View::class.java)
    }

    @Test
    fun testIsLayoutRtl_ltr() {
        Mockito.`when`(view.layoutDirection).thenReturn(View.LAYOUT_DIRECTION_LTR)
        assertFalse(view.isLayoutRtl())
    }

    @Test
    fun testIsLayoutRtl_rtl() {
        Mockito.`when`(view.layoutDirection).thenReturn(View.LAYOUT_DIRECTION_RTL)
        assertTrue(view.isLayoutRtl())
    }

    @Test
    fun testGetTextDirectionHeuristics_ltr() {
        Mockito.`when`(view.layoutDirection).thenReturn(View.LAYOUT_DIRECTION_LTR)
        assertEquals(TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR, view.getTextDirectionHeuristics())
    }

    @Test
    fun testGetTextDirectionHeuristics_rtl() {
        Mockito.`when`(view.layoutDirection).thenReturn(View.LAYOUT_DIRECTION_RTL)
        assertEquals(TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL, view.getTextDirectionHeuristics())
    }

    @Test
    fun testIsTextRTL_on_ltr_text_ltr() {
        Mockito.`when`(view.layoutDirection).thenReturn(View.LAYOUT_DIRECTION_LTR)
        assertFalse(view.isTextRtl("Hello World!"))
    }

    @Test
    fun testIsTextRTL_on_ltr_text_rtl() {
        Mockito.`when`(view.layoutDirection).thenReturn(View.LAYOUT_DIRECTION_LTR)
        assertTrue(view.isTextRtl("مرحبا بالعالم!"))
    }

    @Test
    fun testIsTextRTL_on_rtl_text_rtl() {
        Mockito.`when`(view.layoutDirection).thenReturn(View.LAYOUT_DIRECTION_RTL)
        assertTrue(view.isTextRtl("مرحبا بالعالم!"))
    }

    @Test
    fun testIsTextRTL_on_rtl_text_ltr() {
        Mockito.`when`(view.layoutDirection).thenReturn(View.LAYOUT_DIRECTION_RTL)
        assertFalse(view.isTextRtl("Hello World!"))
    }
}