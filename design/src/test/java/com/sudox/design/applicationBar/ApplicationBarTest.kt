package com.sudox.design.applicationBar

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.sudox.design.DesignTestRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

private const val TITLE_TEXT = "Title"

@RunWith(DesignTestRunner::class)
class ApplicationBarTest : Assert() {

    private lateinit var applicationBar: ApplicationBar
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
        applicationBar = ApplicationBar(activity).apply {
            id = Int.MAX_VALUE
        }

        activity.setContentView(applicationBar)

        if (bundle != null) {
            activityController.restoreInstanceState(bundle)
        }
    }

    @Test
    fun testTitle() {
        applicationBar.setTitleText(TITLE_TEXT)
        assertNotEquals(-1, applicationBar.indexOfChild(applicationBar.titleTextView))
        assertEquals(TITLE_TEXT, applicationBar.titleTextView.text)
        assertEquals(View.VISIBLE, applicationBar.titleTextView.visibility)
    }

    @Test
    fun testResetAllButtons() {
        applicationBar.buttonsAtEnd.forEach { it!!.visibility = View.VISIBLE }
        applicationBar.buttonAtStart!!.visibility = View.VISIBLE

        applicationBar.reset()
        createActivity()

        applicationBar.buttonsAtEnd.forEach { assertEquals(View.GONE, it!!.visibility) }
        assertEquals(View.GONE, applicationBar.buttonAtStart!!.visibility)
    }

    @Test
    fun testViewResetTitleText() {
        applicationBar.setTitleText(TITLE_TEXT)
        applicationBar.reset()
        createActivity()

        assertEquals(-1, applicationBar.indexOfChild(applicationBar.titleTextView))
        assertTrue(TextUtils.isEmpty(applicationBar.titleTextView.text))
    }

    @Test
    fun testViewResetTitleRes() {
        applicationBar.setTitleText(android.R.string.selectAll)
        applicationBar.reset()
        createActivity()

        assertEquals(-1, applicationBar.indexOfChild(applicationBar.titleTextView))
        assertTrue(TextUtils.isEmpty(applicationBar.titleTextView.text))
    }

    @Test
    fun testButtonsListener() {
        val clicks = ArrayList<Int>()

        applicationBar.listener = object : ApplicationBarListener {
            override fun onButtonClicked(tag: Int) {
                clicks.add(tag)
            }
        }

        applicationBar.buttonsAtEnd.forEach { it!!.visibility = View.VISIBLE }
        applicationBar.buttonAtStart!!.visibility = View.VISIBLE

        applicationBar.buttonAtStart!!.performClick()
        applicationBar.buttonsAtEnd[0]!!.performClick()
        applicationBar.buttonsAtEnd[1]!!.performClick()
        applicationBar.buttonsAtEnd[2]!!.performClick()

        assertEquals(4, clicks.size)
        assertEquals(APPBAR_START_BUTTON_TAG, clicks[0])
        assertEquals(APPBAR_FIRST_END_BUTTON_TAG, clicks[1])
        assertEquals(APPBAR_SECOND_END_BUTTON_TAG, clicks[2])
        assertEquals(APPBAR_THIRD_END_BUTTON_TAG, clicks[3])
    }

    @Test
    fun testButtonsIdsSaving() {
        val ids = ArrayList<Int>()

        applicationBar.buttonsAtEnd.forEach { ids.plusAssign(it!!.id) }
        ids.plusAssign(applicationBar.buttonAtStart!!.id)
        createActivity()

        // Verifying ...
        applicationBar.buttonsAtEnd.forEachIndexed { index, element ->
            val id = ids.elementAt(index)

            assertNotEquals(View.NO_ID, id)
            assertEquals(element!!.id, id)
        }

        val id = ids.elementAt(3)
        assertNotEquals(View.NO_ID, id)
        assertEquals(applicationBar.buttonAtStart!!.id, id)
    }

    @Test
    fun testTitleResIdSaving() {
        applicationBar.setTitleText(android.R.string.selectAll)

        createActivity()
        assertEquals("Select all", applicationBar.titleTextView.text)
        assertNotEquals(-1, applicationBar.indexOfChild(applicationBar.titleTextView))
    }
}