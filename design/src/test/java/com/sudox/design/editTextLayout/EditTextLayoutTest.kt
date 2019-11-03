package com.sudox.design.editTextLayout

import android.app.Activity
import android.os.Bundle
import android.widget.EditText
import com.sudox.design.DesignTestRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

@RunWith(DesignTestRunner::class)
class EditTextLayoutTest : Assert() {

    private var editText: EditText? = null
    private var editTextLayout: EditTextLayout? = null
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
            editText = EditText(this).apply {
                id = Int.MAX_VALUE - 1
            }

            editTextLayout = EditTextLayout(this).apply {
                addView(editText!!)
                id = Int.MAX_VALUE
            }

            setContentView(editTextLayout)
        }

        state?.let {
            activityController!!.restoreInstanceState(state)
        }

        activityController!!
                .resume()
                .visible()
    }

    @Test
    fun testStartup() = editTextLayout!!.let {
        assertNull(it.getErrorText())
    }

    @Test
    fun testErrorShowing() = editTextLayout!!.let {
        it.setErrorText("Test")
        assertEquals("Test", it.getErrorText())
    }

    @Test
    fun testErrorShowingById() {
        val valid = editTextLayout!!.context.getString(android.R.string.cut)

        editTextLayout!!.setErrorText(android.R.string.cut)
        assertEquals(valid, editTextLayout!!.getErrorText())
    }

    @Test
    fun testErrorShowingWithoutText() = editTextLayout!!.let {
        it.setErrorText("")
        assertEquals("", it.getErrorText())
    }

    @Test
    fun testErrorResetting() = editTextLayout!!.let {
        it.setErrorText("Test")
        it.setErrorText(null)

        assertNull(it.getErrorText())
    }

    @Test
    fun testStateSaving() {
        val valid = editTextLayout!!.context.getString(android.R.string.cut)

        editTextLayout!!.setErrorText(android.R.string.cut)
        createActivity()

        assertEquals(valid, editTextLayout!!.getErrorText())
    }
}