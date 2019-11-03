package com.sudox.design.applicationBar.applicationBarButton

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.sudox.design.DesignTestRunner
import com.sudox.design.R
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

private const val BUTTON_TEXT = "Text"
private val BUTTON_ICON_DRAWABLE = R.drawable.abc_vector_test

@RunWith(DesignTestRunner::class)
class ApplicationBarButtonTest : Assert() {

    private lateinit var navigationBarButton: ApplicationBarButton
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    @Before
    fun setUp() {
        createActivity()
    }

    private fun createActivity() {
        val bundle: Bundle? = if (::activityController.isInitialized) {
            Bundle().apply {
                activityController.saveInstanceState(this)
            }
        } else {
            null
        }

        activityController = Robolectric
                .buildActivity(Activity::class.java)
                .start()
                .visible()

        activity = activityController.get()
        navigationBarButton = ApplicationBarButton(activity).apply {
            id = Int.MAX_VALUE
        }

        activity.setContentView(navigationBarButton)

        if (bundle != null) {
            activityController.restoreInstanceState(bundle)
        }
    }

    @Test
    fun testViewReset_BasicParams() {
        navigationBarButton.isClickable = true
        navigationBarButton.visibility = View.VISIBLE
        navigationBarButton.iconDirection = ApplicationBarButtonIconDirection.END
        navigationBarButton.reset()
        createActivity()

        assertFalse(navigationBarButton.isClickable)
        assertEquals(View.GONE, navigationBarButton.visibility)
        assertEquals(ApplicationBarButtonIconDirection.START, navigationBarButton.iconDirection)
    }

    @Test
    fun testViewResetMainParamsNotFromRes() {
        navigationBarButton.setText(BUTTON_TEXT)
        navigationBarButton.setIconDrawable(ColorDrawable(Color.BLACK))
        navigationBarButton.reset()
        createActivity()

        assertNull(navigationBarButton.iconDrawable)
        assertNull(navigationBarButton.text)
    }

    @Test
    fun testViewResetMainParamsFromRes() {
        navigationBarButton.setText(android.R.string.selectTextMode)
        navigationBarButton.setIconDrawable(BUTTON_ICON_DRAWABLE)
        navigationBarButton.reset()
        createActivity()

        assertNull(navigationBarButton.iconDrawable)
        assertNull(navigationBarButton.text)
    }

    @Test
    fun testBasicParamsSaving() {
        navigationBarButton.isClickable = true
        navigationBarButton.visibility = View.VISIBLE
        navigationBarButton.iconDirection = ApplicationBarButtonIconDirection.END
        createActivity()

        assertTrue(navigationBarButton.isClickable)
        assertEquals(View.VISIBLE, navigationBarButton.visibility)
        assertEquals(ApplicationBarButtonIconDirection.END, navigationBarButton.iconDirection)
    }

    @Test
    fun testSavingTextFromRes() {
        navigationBarButton.setText(android.R.string.selectTextMode)
        createActivity()

        assertEquals(android.R.string.selectTextMode, navigationBarButton.textRes)
        assertEquals("Select text", navigationBarButton.text)
    }

    @Test
    fun testSavingDrawableFromRes() {
        navigationBarButton.setIconDrawable(BUTTON_ICON_DRAWABLE)
        createActivity()

        assertEquals(BUTTON_ICON_DRAWABLE, navigationBarButton.iconDrawableRes)
    }
}