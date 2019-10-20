package com.sudox.design.applicationBar.applicationBarButton

import android.app.Activity
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import com.sudox.design.DesignTestRunner
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
    fun testStartup() = applicationBarButton!!.let { button ->
        assertEquals(0, button.iconDrawableId)
        assertFalse(button.isClickable)
        assertNull(button.iconDrawable)
    }

    @Test
    fun testEnabling() = applicationBarButton!!.let { button ->
        button.toggle(ShapeDrawable())

        button.iconDrawable!!.let { drawable ->
            assertEquals(button.iconWidth, drawable.bounds.width())
            assertEquals(button.iconHeight, drawable.bounds.height())
        }

        assertEquals(0, button.iconDrawableId)
        assertTrue(button.isClickable)
    }

    @Test
    fun testDisabling() = applicationBarButton!!.let { button ->
        button.toggle(android.R.drawable.ic_delete)
        button.toggle(null)

        assertEquals(0, button.iconDrawableId)
        assertFalse(button.isClickable)
        assertNull(button.iconDrawable)
    }

    @Test
    fun testSettingIconById() = applicationBarButton!!.let { button ->
        button.toggle(android.R.drawable.ic_delete)

        button.iconDrawable!!.let { drawable ->
            assertEquals(button.iconWidth, drawable.bounds.width())
            assertEquals(button.iconHeight, drawable.bounds.height())
        }

        assertEquals(android.R.drawable.ic_delete, button.iconDrawableId)
        assertNotNull(button.iconDrawable)
        assertTrue(button.isClickable)
    }

    @Test
    fun testStateSaving() {
        applicationBarButton!!.toggle(android.R.drawable.ic_delete)
        createActivity()

        assertEquals(android.R.drawable.ic_delete, applicationBarButton!!.iconDrawableId)
        assertNotNull(applicationBarButton!!.iconDrawable)
        assertTrue(applicationBarButton!!.isClickable)
    }
}