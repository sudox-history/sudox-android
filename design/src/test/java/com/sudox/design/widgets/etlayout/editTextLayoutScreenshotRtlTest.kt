package com.sudox.design.widgets.etlayout

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.sudox.design.DesignTestRunner
import com.sudox.design.R
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

private const val EDITTEXT_HINT = "Test hint"
private const val ERROR_TEXT = "Error"

@RunWith(DesignTestRunner::class)
class EditTextLayoutTest : Assert() {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity
    private lateinit var editTextLayout: EditTextLayout
    private lateinit var editText: EditText

    @Before
    fun setUp() {
        createActivity()
    }

    @Test
    fun testStartup() {
        assertEquals(EDITTEXT_HINT, editTextLayout.label!!.originalText)
        assertEquals(EDITTEXT_HINT, editTextLayout.label!!.getCurrentText())
        assertEquals(editText, editTextLayout.editText)
        assertTrue(TextUtils.isEmpty(editText.hint))
    }

    @Test
    fun testStateFocused() {
        editText.requestFocus()
        assertEquals(editText.currentTextColor, editTextLayout.label!!.getCurrentColor())
        assertEquals(EDITTEXT_HINT, editTextLayout.label!!.getCurrentText())

        editText.isEnabled = false
        assertEquals(editText.currentHintTextColor, editTextLayout.label!!.getCurrentColor())
        assertEquals(EDITTEXT_HINT, editTextLayout.label!!.getCurrentText())
    }

    @Test
    fun testStateError() {
        editTextLayout.setErrorText(ERROR_TEXT)
        assertEquals(editTextLayout.label!!.params.errorTextColor, editTextLayout.label!!.getCurrentColor())
        assertEquals(ERROR_TEXT, editTextLayout.label!!.getCurrentText())

        editText.isEnabled = false
        assertEquals(editText.currentHintTextColor, editTextLayout.label!!.getCurrentColor())
        assertEquals(EDITTEXT_HINT, editTextLayout.label!!.getCurrentText())

        editText.isEnabled = true
        assertEquals(editTextLayout.label!!.params.errorTextColor, editTextLayout.label!!.getCurrentColor())
        assertEquals(ERROR_TEXT, editTextLayout.label!!.getCurrentText())

        editText.setText("Changed text!")
        assertEquals(editText.currentHintTextColor, editTextLayout.label!!.getCurrentColor())
        assertEquals(EDITTEXT_HINT, editTextLayout.label!!.getCurrentText())
    }

    @Test
    fun testErrorTextSaving() {
        editTextLayout.setErrorText(ERROR_TEXT)

        createActivity()
        assertEquals(editTextLayout.label!!.params.errorTextColor, editTextLayout.label!!.getCurrentColor())
        assertEquals(ERROR_TEXT, editTextLayout.label!!.getCurrentText())
    }

    @Test
    fun testErrorTextResSaving() {
        editTextLayout.setErrorTextRes(R.string.test_string)

        createActivity()
        assertEquals(editTextLayout.label!!.params.errorTextColor, editTextLayout.label!!.getCurrentColor())
        assertEquals("QWERTYUIOP", editTextLayout.label!!.getCurrentText())
    }

    @Test
    fun testLabelTextSaving() {
        createActivity()
        assertEquals(editText.currentHintTextColor, editTextLayout.label!!.getCurrentColor())
        assertEquals(EDITTEXT_HINT, editTextLayout.label!!.getCurrentText())
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
        createElements()
        activity.setContentView(editTextLayout)

        if (bundle != null) {
            activityController.restoreInstanceState(bundle)
        }
    }

    private fun createElements() {
        editText = EditText(activity)
        editText.id = 1
        editText.hint = EDITTEXT_HINT
        editText.visibility = View.VISIBLE
        editText.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

        editTextLayout = EditTextLayout(activity)
        editTextLayout.id = 2
        editTextLayout.visibility = View.VISIBLE
        editTextLayout.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

        editTextLayout.addView(editText)
    }
}