package com.sudox.design.applicationBar.applicationBarButton

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.sudox.design.DesignTestRunner
import com.sudox.design.R
import com.sudox.design.phoneNumberUtil
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

@RunWith(DesignTestRunner::class)
class ApplicationBarButtonTest : Assert() {

    private var applicationBarButton: ApplicationBarButton? = null
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
            applicationBarButton = ApplicationBarButton(this).apply {
                id = Int.MAX_VALUE
            }

            phoneNumberUtil = PhoneNumberUtil.createInstance(this)
            setContentView(applicationBarButton)
        }

        state?.let {
            activityController!!.restoreInstanceState(state)
        }

        activityController!!
                .resume()
                .visible()
    }

    @Test
    fun testViewResetting() {
        applicationBarButton!!.isClickable = true
        applicationBarButton!!.visibility = View.VISIBLE
        applicationBarButton!!.iconDirection = ApplicationBarButtonIconDirection.END
        applicationBarButton!!.setText(android.R.string.selectTextMode)
        applicationBarButton!!.setIconDrawable(R.drawable.abc_vector_test)
        applicationBarButton!!.reset()
        createActivity()

        assertFalse(applicationBarButton!!.isClickable)
        assertEquals(View.GONE, applicationBarButton!!.visibility)
        assertEquals(ApplicationBarButtonIconDirection.START, applicationBarButton!!.iconDirection)
        assertNull(applicationBarButton!!.iconDrawable)
        assertNull(applicationBarButton!!.text)
    }

    @Test
    fun testStateSaving() {
        applicationBarButton!!.isClickable = true
        applicationBarButton!!.visibility = View.VISIBLE
        applicationBarButton!!.iconDirection = ApplicationBarButtonIconDirection.END
        applicationBarButton!!.setText(android.R.string.selectTextMode)
        applicationBarButton!!.setIconDrawable(R.drawable.abc_vector_test)
        createActivity()

        assertTrue(applicationBarButton!!.isClickable)
        assertEquals(View.VISIBLE, applicationBarButton!!.visibility)
        assertEquals(ApplicationBarButtonIconDirection.END, applicationBarButton!!.iconDirection)
        assertEquals(android.R.string.selectTextMode, applicationBarButton!!.textRes)
        assertEquals("Select text", applicationBarButton!!.text)
    }
}