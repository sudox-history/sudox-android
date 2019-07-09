package com.sudox.design.helpers

import android.app.Activity
import android.util.LayoutDirection
import android.view.View
import com.sudox.design.DesignTestRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

@RunWith(DesignTestRunner::class)
class RTLHelperTest : Assert() {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity
    private lateinit var view: View

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()
        view = View(activity)
    }

    @Test
    fun testLayoutWhenRTL() {
        view.layoutDirection = LayoutDirection.RTL
        assertTrue(view.isLayoutRtl())
    }

    @Test
    fun testLayoutWhenLTR() {
        view.layoutDirection = LayoutDirection.LTR
        assertFalse(view.isLayoutRtl())
    }

    @Test
    fun testTextWhenRTL() {
        view.layoutDirection = LayoutDirection.RTL
        assertTrue(view.isTextRtl("مرحبا بالعالم!"))
        assertFalse(view.isTextRtl("Hello World!"))
    }

    @Test
    fun testTextWhenLTR() {
        view.layoutDirection = LayoutDirection.LTR
        assertTrue(view.isTextRtl("مرحبا بالعالم!"))
        assertFalse(view.isTextRtl("Hello World!"))
    }
}