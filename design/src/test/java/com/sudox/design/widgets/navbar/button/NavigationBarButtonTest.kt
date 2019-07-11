package com.sudox.design.widgets.navbar.button

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
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
class NavigationBarButtonTest : Assert() {

    private lateinit var navigationBarButton: NavigationBarButton
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    @Before
    fun setUp() {
        createActivity()
    }

    private fun createActivity() {
        val params = NavigationBarButtonParams().apply {
            textTypeface = Typeface.DEFAULT
        }

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
        navigationBarButton = NavigationBarButton(activity, params).apply { id = 1 }
        activity.setContentView(navigationBarButton)

        if (bundle != null) {
            activityController.restoreInstanceState(bundle)
        }
    }

    @Test
    fun testStartup() {
        assertFalse(navigationBarButton.isClickable)
        assertEquals(View.GONE, navigationBarButton.visibility)
        assertEquals(NavigationBarButtonIconDirection.DEFAULT, navigationBarButton.iconDirection)
        assertNull(navigationBarButton.iconDrawable)
        assertNull(navigationBarButton.text)
    }

    @Test
    fun testViewReset_BasicParams() {
        navigationBarButton.isClickable = true
        navigationBarButton.visibility = View.VISIBLE
        navigationBarButton.iconDirection = NavigationBarButtonIconDirection.END
        navigationBarButton.resetView()
        createActivity()

        assertFalse(navigationBarButton.isClickable)
        assertEquals(View.GONE, navigationBarButton.visibility)
        assertEquals(NavigationBarButtonIconDirection.DEFAULT, navigationBarButton.iconDirection)
    }

    @Test
    fun testViewResetMainParamsNotFromRes() {
        navigationBarButton.setText(BUTTON_TEXT)
        navigationBarButton.setIconDrawable(ColorDrawable(Color.BLACK))
        navigationBarButton.resetView()
        createActivity()

        assertNull(navigationBarButton.iconDrawable)
        assertNull(navigationBarButton.text)
    }

    @Test
    fun testViewResetMainParamsFromRes() {
        navigationBarButton.setTextRes(R.string.test_string)
        navigationBarButton.setIconDrawableRes(BUTTON_ICON_DRAWABLE)
        navigationBarButton.resetView()
        createActivity()

        assertNull(navigationBarButton.iconDrawable)
        assertNull(navigationBarButton.text)
    }

    @Test
    fun testBasicParamsSaving() {
        navigationBarButton.isClickable = true
        navigationBarButton.visibility = View.VISIBLE
        navigationBarButton.iconDirection = NavigationBarButtonIconDirection.END
        createActivity()

        assertTrue(navigationBarButton.isClickable)
        assertEquals(View.VISIBLE, navigationBarButton.visibility)
        assertEquals(NavigationBarButtonIconDirection.END, navigationBarButton.iconDirection)
    }

    @Test
    fun testSavingTextNotFromRes() {
        navigationBarButton.setText(BUTTON_TEXT)
        createActivity()

        assertEquals(BUTTON_TEXT, navigationBarButton.text)
    }

    @Test
    fun testSavingTextFromRes() {
        navigationBarButton.setTextRes(R.string.test_string)
        createActivity()

        assertEquals(R.string.test_string, navigationBarButton.textRes)
        assertEquals("QWERTYUIOP", navigationBarButton.text)
    }

    @Test
    fun testSavingDrawableFromRes() {
        navigationBarButton.setIconDrawableRes(BUTTON_ICON_DRAWABLE)
        createActivity()

        assertEquals(BUTTON_ICON_DRAWABLE, navigationBarButton.iconDrawableRes)
    }
}