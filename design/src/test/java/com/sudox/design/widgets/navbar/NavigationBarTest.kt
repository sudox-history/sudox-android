package com.sudox.design.widgets.navbar

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.sudox.design.DesignTestRunner
import com.sudox.design.R
import com.sudox.design.shadows.TagViewShadow
import com.sudox.design.shadows.ViewGroupShadow
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

private const val TITLE_TEXT = "Title"

@RunWith(DesignTestRunner::class)
@Config(shadows = [
    ViewGroupShadow::class,
    TagViewShadow::class
])
class NavigationBarTest : Assert() {

    private lateinit var navigationBar: NavigationBar
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
        navigationBar = NavigationBar(activity).apply {
            id = 1
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        activity.setContentView(navigationBar)

        if (bundle != null) {
            activityController.restoreInstanceState(bundle)
        }
    }

    @Test
    fun testStartup() {
        assertEquals(View.GONE, navigationBar.buttonStart!!.visibility)
        assertEquals(-1, navigationBar.indexOfChild(navigationBar.titleTextView))

        navigationBar.buttonsEnd.forEach {
            assertEquals(View.GONE, it!!.visibility)
        }
    }

    @Test
    fun testTitle() {
        navigationBar.setTitleText(TITLE_TEXT)
        assertNotEquals(-1, navigationBar.indexOfChild(navigationBar.titleTextView))
        assertEquals(TITLE_TEXT, navigationBar.titleTextView.text)
        assertEquals(View.VISIBLE, navigationBar.titleTextView.visibility)
    }

    @Test
    fun testResetAllButtons() {
        navigationBar.buttonsEnd.forEach { it!!.visibility = View.VISIBLE }
        navigationBar.buttonStart!!.visibility = View.VISIBLE

        navigationBar.resetView()
        createActivity()

        navigationBar.buttonsEnd.forEach { assertEquals(View.GONE, it!!.visibility) }
        assertEquals(View.GONE, navigationBar.buttonStart!!.visibility)
    }

    @Test
    fun testViewResetTitleText() {
        navigationBar.setTitleText(TITLE_TEXT)
        navigationBar.resetView()
        createActivity()

        assertEquals(-1, navigationBar.indexOfChild(navigationBar.titleTextView))
        assertTrue(TextUtils.isEmpty(navigationBar.titleTextView.text))
    }

    @Test
    fun testViewResetTitleRes() {
        navigationBar.setTitleTextRes(R.string.test_string)
        navigationBar.resetView()
        createActivity()

        assertEquals(-1, navigationBar.indexOfChild(navigationBar.titleTextView))
        assertTrue(TextUtils.isEmpty(navigationBar.titleTextView.text))
    }

    @Test
    fun testButtonsListener() {
        val clicks = ArrayList<Int>()

        navigationBar.buttonsClickCallback = { clicks.add(it) }
        navigationBar.buttonsEnd.forEach { it!!.visibility = View.VISIBLE }
        navigationBar.buttonStart!!.visibility = View.VISIBLE

        navigationBar.buttonStart!!.performClick()
        navigationBar.buttonsEnd[0]!!.performClick()
        navigationBar.buttonsEnd[1]!!.performClick()
        navigationBar.buttonsEnd[2]!!.performClick()

        assertEquals(4, clicks.size)
        assertEquals(NAVBAR_START_BUTTON_TAG, clicks[0])
        assertEquals(NAVBAR_FIRST_END_BUTTON_TAG, clicks[1])
        assertEquals(NAVBAR_SECOND_END_BUTTON_TAG, clicks[2])
        assertEquals(NAVBAR_THIRD_END_BUTTON_TAG, clicks[3])
    }

    @Test
    fun testButtonsIdsSaving() {
        val ids = ArrayList<Int>()

        navigationBar.buttonsEnd.forEach { ids.plusAssign(it!!.id) }
        ids.plusAssign(navigationBar.buttonStart!!.id)
        createActivity()

        // Verifying ...
        navigationBar.buttonsEnd.forEachIndexed { index, element ->
            val id = ids.elementAt(index)

            assertNotEquals(View.NO_ID, id)
            assertEquals(element!!.id, id)
        }

        val id = ids.elementAt(3)
        assertNotEquals(View.NO_ID, id)
        assertEquals(navigationBar.buttonStart!!.id, id)
    }

    @Test
    fun testTitleResIdSaving() {
        navigationBar.setTitleTextRes(R.string.test_string)

        createActivity()
        assertEquals("QWERTYUIOP", navigationBar.titleTextView.text)
        assertNotEquals(-1, navigationBar.indexOfChild(navigationBar.titleTextView))
    }

    @Test
    fun testTitleTextSaving() {
        navigationBar.setTitleText(TITLE_TEXT)

        createActivity()
        assertEquals(TITLE_TEXT, navigationBar.titleTextView.text)
        assertNotEquals(-1, navigationBar.indexOfChild(navigationBar.titleTextView))
    }
}