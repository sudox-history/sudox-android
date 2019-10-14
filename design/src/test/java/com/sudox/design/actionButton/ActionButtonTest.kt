package com.sudox.design.actionButton

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
class ActionButtonTest : Assert() {

    private var actionButton: ActionButton? = null
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
            actionButton = ActionButton(this).apply {
                id = Int.MAX_VALUE
            }

            phoneNumberUtil = PhoneNumberUtil.createInstance(this)
            setContentView(actionButton)
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
        actionButton!!.toggleLoadingState(true)
        Robolectric.flushForegroundThreadScheduler()

        assertFalse(actionButton!!.isClickable)
        assertTrue(actionButton!!.isLoadingState())
    }

    @Test
    fun testStateSaving() {
        actionButton!!.toggleLoadingState(true)
        createActivity()

        assertFalse(actionButton!!.isClickable)
        assertTrue(actionButton!!.isLoadingState())
        assertTrue(actionButton!!.loadingSpinnerDrawable.isRunning)
    }
}