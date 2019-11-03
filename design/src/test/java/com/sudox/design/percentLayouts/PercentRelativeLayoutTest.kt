package com.sudox.design.percentLayouts

import android.app.Activity
import android.os.Bundle
import com.sudox.design.DesignTestRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

@RunWith(DesignTestRunner::class)
class PercentRelativeLayoutTest : Assert() {

    private var percentRelativeLayout: PercentRelativeLayout? = null
    private var activityController: ActivityController<Activity>? = null

    @Before
    fun setUp() {
        createActivity()
    }

    private fun createActivity() {
        var state: Bundle? = null

        activityController?.let {
            state = Bundle()

            it.saveInstanceState(state)
            it.pause()
            it.stop()
            it.destroy()
        }

        activityController = Robolectric
                .buildActivity(Activity::class.java)
                .create()
                .start()

        activityController!!.get().apply {
            percentRelativeLayout = PercentRelativeLayout(this).apply {
                id = Int.MAX_VALUE
            }

            setContentView(percentRelativeLayout)
        }

        state?.let {
            activityController!!.restoreInstanceState(state)
        }

        activityController!!
                .resume()
                .visible()
    }

    @Test
    fun testPaddingCalculation() {
        percentRelativeLayout!!.apply {
            paddingTopPercent = 0.1F
            paddingBottomPercent = 0.2F
            paddingRightPercent = 0.3F
            paddingLeftPercent = 0.4F

            requestLayout()
        }

        assertEquals(47, percentRelativeLayout!!.paddingTop)
        assertEquals(94, percentRelativeLayout!!.paddingBottom)
        assertEquals(96, percentRelativeLayout!!.paddingRight)
        assertEquals(128, percentRelativeLayout!!.paddingLeft)
    }

    @Test
    fun testDoublePaddingCalculation() {
        percentRelativeLayout!!.apply {
            paddingTopPercent = 0.1F
            paddingBottomPercent = 0.2F
            paddingRightPercent = 0.3F
            paddingLeftPercent = 0.4F

            requestLayout()
            requestLayout()
        }

        assertEquals(47, percentRelativeLayout!!.paddingTop)
        assertEquals(94, percentRelativeLayout!!.paddingBottom)
        assertEquals(96, percentRelativeLayout!!.paddingRight)
        assertEquals(128, percentRelativeLayout!!.paddingLeft)
    }
}