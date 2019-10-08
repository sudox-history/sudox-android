package com.sudox.design.outline

import android.app.Activity
import android.content.Context
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.sudox.design.DesignTestRunner
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(DesignTestRunner::class)
class OffsettedOutlineProviderTest {

    private var context: Context? = null
    private var provider: OffsettedOutlineProvider? = null

    @Before
    fun setUp() {
        context = Robolectric.buildActivity(Activity::class.java).get()
        provider = OffsettedOutlineProvider()
    }

    @Test
    fun testGetOutline() {
        val outline = Outline()
        val view = OffsettedOutlineTestView(context!!).apply {
            background = ColorDrawable().apply {
                bounds = Rect(0, 0, 50, 50)
            }
        }

        provider!!.getOutline(view, outline)

        val rect = Rect().apply {
            outline.getRect(this)
        }

        assertEquals(40, rect.left)
        assertEquals(80, rect.right)
        assertEquals(50, rect.top)
        assertEquals(60, rect.bottom)
    }

    class OffsettedOutlineTestView(context: Context) : OffsettedOutlineView, View(context) {
        override fun getTopOutlineOffset(): Int = 50
        override fun getBottomOutlineOffset(): Int = 10
        override fun getLeftOutlineOffset(): Int = 40
        override fun getRightOutlineOffset(): Int = 30
    }
}