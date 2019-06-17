package com.sudox.design.helpers

import android.graphics.Typeface
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TypefacesHelperTest : Assert() {

    @Test
    fun testLoadTypeface() {
        val validTypeface = Typeface.create("sans-serif", Typeface.BOLD)
        val typefaceFirst = loadTypeface("sans-serif", Typeface.BOLD)

        assertEquals(validTypeface, typefaceFirst)
        assertTrue(typefaces[getTypefaceHashCode("sans-serif", Typeface.BOLD)] != null)

        val typefaceSecond = loadTypeface("sans-serif", Typeface.BOLD)
        assertEquals(typefaceFirst, typefaceSecond)
    }
}