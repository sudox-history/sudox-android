package com.sudox.design.helpers

import android.graphics.Typeface
import com.sudox.design.DesignTestRunner
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(DesignTestRunner::class)
class TypefacesHelperTest : Assert() {

    @Test
    fun testLoading() {
        val valid = Typeface.create("sans-serif", Typeface.BOLD)
        val first = loadTypeface("sans-serif", Typeface.BOLD)
        val second = loadTypeface("sans-serif", Typeface.BOLD)

        assertEquals(valid, first)
        assertEquals(valid, second)
        assertTrue(first === second)
    }
}