package com.sudox.design.applicationBar

import android.app.Activity
import android.os.Bundle
import com.sudox.design.DesignTestRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

@RunWith(DesignTestRunner::class)
class ApplicationBarTest : Assert() {

    private var applicationBar: ApplicationBar? = null
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
            applicationBar = ApplicationBar(this).apply {
                id = Int.MAX_VALUE
            }

            setContentView(applicationBar)
        }

        state?.let {
            activityController!!.restoreInstanceState(state)
        }

        activityController!!
                .resume()
                .visible()
    }

    @Test
    fun testStartup() = applicationBar!!.let {
        assertNull(it.contentView)
        assertNull(it.titleTextView.parent)
        assertNotEquals(it.titleTextView, it.contentView)
        assertEquals(0, it.titleTextId)
    }

    @Test
    fun testTitleSetting() = applicationBar!!.let {
        it.setTitle("Title")

        assertEquals(it.titleTextView, it.contentView)
        assertEquals("Title", it.titleTextView.text.toString())
        assertEquals(it, it.contentView!!.parent)
        assertEquals(0, it.titleTextId)
    }

    @Test
    fun testTitleSettingById() = applicationBar!!.let {
        it.setTitle(android.R.string.selectAll)

        assertEquals(it.titleTextView, it.contentView)
        assertEquals(it.context.getString(android.R.string.selectAll), it.titleTextView.text.toString())
        assertEquals(it, it.contentView!!.parent)
        assertEquals(android.R.string.selectAll, it.titleTextId)
    }

    @Test
    fun testTitleHiding() = applicationBar!!.let {
        it.setTitle(android.R.string.selectAll)
        it.setTitle(null)

        assertNull(it.contentView)
        assertNull(it.titleTextView.parent)
        assertNotEquals(it.titleTextView, it.contentView)
        assertEquals(0, it.titleTextId)
    }

    @Test
    fun testResetting() = applicationBar!!.let {
        it.setTitle(android.R.string.selectAll)
        it.buttonAtStart!!.toggle(android.R.drawable.ic_input_add)
        it.buttonAtEnd!!.toggle(android.R.drawable.ic_input_delete)
        it.reset()

        assertNull(it.contentView)
        assertNull(it.titleTextView.parent)
        assertNotEquals(it.titleTextView, it.contentView)
        assertEquals(0, it.titleTextId)

        assertEquals(0, it.buttonAtStart!!.iconDrawableId)
        assertNull(it.buttonAtStart!!.iconDrawable)
        assertFalse(it.buttonAtStart!!.isClickable)

        assertEquals(0, it.buttonAtEnd!!.iconDrawableId)
        assertNull(it.buttonAtEnd!!.iconDrawable)
        assertFalse(it.buttonAtEnd!!.isClickable)
    }

    @Test
    fun testListener() = applicationBar!!.let {
        val tags = ArrayList<Any>()

        it.listener = object : ApplicationBarListener {
            override fun onButtonClicked(tag: Any) {
                tags.add(tag)
            }
        }

        it.buttonAtStart!!.toggle(android.R.drawable.ic_input_add)
        it.buttonAtEnd!!.toggle(android.R.drawable.ic_input_delete)

        it.buttonAtStart!!.performClick()
        it.buttonAtEnd!!.performClick()

        assertEquals(APPBAR_BUTTON_AT_START_TAG, tags[0])
        assertEquals(APPBAR_BUTTON_AT_END_TAG, tags[1])
    }

    @Test
    fun testStateSaving() {
        applicationBar!!.setTitle(android.R.string.selectAll)
        applicationBar!!.buttonAtStart!!.toggle(android.R.drawable.ic_input_add)
        applicationBar!!.buttonAtEnd!!.toggle(android.R.drawable.ic_input_delete)
        createActivity()

        assertEquals(applicationBar!!.titleTextView, applicationBar!!.contentView)
        assertEquals(applicationBar!!.context.getString(android.R.string.selectAll), applicationBar!!.titleTextView.text.toString())
        assertEquals(applicationBar!!, applicationBar!!.contentView!!.parent)
        assertEquals(android.R.string.selectAll, applicationBar!!.titleTextId)

        assertEquals(android.R.drawable.ic_input_add, applicationBar!!.buttonAtStart!!.iconDrawableId)
        assertEquals(android.R.drawable.ic_input_delete, applicationBar!!.buttonAtEnd!!.iconDrawableId)

        assertTrue(applicationBar!!.buttonAtStart!!.isClickable)
        assertTrue(applicationBar!!.buttonAtEnd!!.isClickable)

        assertNotNull(applicationBar!!.buttonAtStart!!.iconDrawable)
        assertNotNull(applicationBar!!.buttonAtEnd!!.iconDrawable)
    }
}