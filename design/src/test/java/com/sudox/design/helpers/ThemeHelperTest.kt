package com.sudox.design.helpers

import android.R
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.sudox.design.DesignTestRunner
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment

@RunWith(DesignTestRunner::class)
class ThemeHelperTest : Assert() {

    @Test
    fun testControlHighlightColor() {
        val context = RuntimeEnvironment.application
        val resourceId = with(TypedValue()) {
            context.theme.resolveAttribute(R.attr.colorControlHighlight, this, true)
            resourceId
        }

        val result = context.theme.getControlHighlightColor()
        val valid = ContextCompat.getColor(context, resourceId)
        assertEquals(valid, result)
    }
}