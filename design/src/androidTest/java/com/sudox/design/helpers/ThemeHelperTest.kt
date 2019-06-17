package com.sudox.design.helpers

import android.content.Context
import android.util.TypedValue
import androidx.test.platform.app.InstrumentationRegistry
import com.sudox.design.R
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ThemeHelperTest : Assert() {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        context.setTheme(R.style.SudoxTheme)
    }

    @Test
    fun testGetControlHighlightColor() {
        val resourceId = with(TypedValue()) {
            context.theme.resolveAttribute(R.attr.colorControlHighlight, this, true)
            resourceId
        }

        val result = context.theme.getControlHighlightColor()
        val valid = context.getColor(resourceId)
        assertEquals(valid, result)
    }
}