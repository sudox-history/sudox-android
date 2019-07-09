package com.sudox.design.widgets.navbar

import android.app.Activity
import android.view.View
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class NavigationBarTest : Assert() {

    private lateinit var navigationBar: NavigationBar
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    @Before
    fun setUp() {
        activityController = Robolectric
                .buildActivity(Activity::class.java)
                .start()
                .visible()

        activity = activityController.get()
        navigationBar = NavigationBar(activity)
        activity.setContentView(navigationBar)
    }

    @Test
    fun testStartup() {
        assertNull(navigationBar.contentView)
        assertEquals(View.GONE, navigationBar.buttonStart!!.visibility)
        assertThat()
//        assertEquals(View.GONE, navigationBar.buttonStart!!.visibility)
    }
}