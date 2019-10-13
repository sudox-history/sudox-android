package com.sudox.design.button

import android.app.Activity
import android.os.Bundle
import com.sudox.design.DesignTestRunner
import com.sudox.design.phoneNumberUtil
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

@RunWith(DesignTestRunner::class)
class SudoxButtonTest : Assert() {

    private var sudoxButton: SudoxButton? = null
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
            sudoxButton = SudoxButton(this).apply {
                id = Int.MAX_VALUE
            }

            phoneNumberUtil = PhoneNumberUtil.createInstance(this)
            setContentView(sudoxButton)
        }

        state?.let {
            activityController!!.restoreInstanceState(state)
        }

        activityController!!
                .resume()
                .visible()
    }

    @Test
    fun testNormalToLoadingStateToggling() {
        sudoxButton!!.toggleLoadingState(true)
        Robolectric.flushForegroundThreadScheduler()

        assertFalse(sudoxButton!!.isClickable)
        assertTrue(sudoxButton!!.isInLoadingState())
    }

    @Test
    fun testStateSaving() {
        sudoxButton!!.toggleLoadingState(true)
        createActivity()

        assertFalse(sudoxButton!!.isClickable)
        assertTrue(sudoxButton!!.isInLoadingState())
        assertTrue(sudoxButton!!.loadingSpinnerDrawable.isRunning)
    }
}