package com.sudox.design.widgets.etlayout

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.filters.LargeTest
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.novoda.espresso.ViewCreator
import com.novoda.espresso.ViewTestRule
import com.sudox.design.R
import org.hamcrest.CoreMatchers.*
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class EditTextLayoutTest : Assert() {

    private lateinit var linkedEditText: EditText

    @Rule
    @JvmField
    val viewTestRule = ViewTestRule<EditTextLayout>(ViewCreator<EditTextLayout> { context, _ ->
        return@ViewCreator EditTextLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            linkedEditText = EditText(context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                hint = "Test"
            }

            addView(linkedEditText)
        }
    })

    @Test
    fun testOnStartup() {
        val view = viewTestRule.view

        assertNotNull(view.label)
        assertEquals("Test", view.label!!.originalText)
        onView(createMatcher()).check(matches(not(withHint("Test"))))
    }

    @Test
    fun testDisabling() {
        val view = viewTestRule.view

        viewTestRule.runOnMainSynchronously { linkedEditText.isEnabled = false }

        assertEquals("Test", view.label!!.getCurrentText())
        assertEquals(linkedEditText.currentHintTextColor, view.label!!.getCurrentColor())
    }

    @Test
    fun testError() {
        val view = viewTestRule.view

        viewTestRule.runOnMainSynchronously { view.setErrorText("Error") }
        assertEquals("Error", view.label!!.getCurrentText())
        assertEquals(view.label!!.params.errorTextColor, view.label!!.getCurrentColor())

        viewTestRule.runOnMainSynchronously { view.setErrorTextRes(R.string.test_string) }
        assertEquals("QWERTYUIOP", view.label!!.getCurrentText())
        assertEquals(view.label!!.params.errorTextColor, view.label!!.getCurrentColor())

        viewTestRule.runOnMainSynchronously { linkedEditText.isEnabled = false }
        assertEquals("Test", view.label!!.getCurrentText())
        assertEquals(linkedEditText.currentHintTextColor, view.label!!.getCurrentColor())

        viewTestRule.runOnMainSynchronously { linkedEditText.isEnabled = true }
        assertEquals("QWERTYUIOP", view.label!!.getCurrentText())
        assertEquals(view.label!!.params.errorTextColor, view.label!!.getCurrentColor())

        viewTestRule.runOnMainSynchronously { view.resetErrorText() }
        assertEquals("Test", view.label!!.getCurrentText())
        assertEquals(linkedEditText.currentHintTextColor, view.label!!.getCurrentColor())

        viewTestRule.runOnMainSynchronously { view.setErrorText("Error") }
        onView(allOf(`is`(linkedEditText as View))).perform(click(), replaceText("Не ори. (c) TheMax"))
        assertEquals("Test", view.label!!.getCurrentText())
        assertEquals(linkedEditText.currentTextColor, view.label!!.getCurrentColor())

        viewTestRule.runOnMainSynchronously { view.setErrorText("Error") }
        onView(allOf(`is`(linkedEditText as View))).perform(click(), replaceText("Не ори. (c) TheMax in August 2018"))
        assertEquals("Test", view.label!!.getCurrentText())
        assertEquals(linkedEditText.currentTextColor, view.label!!.getCurrentColor())

        onView(allOf(`is`(linkedEditText as View))).perform(click(), replaceText(""))
        viewTestRule.runOnMainSynchronously { view.setErrorText("Error") }
        onView(allOf(`is`(linkedEditText as View))).perform(click(), replaceText(""))
        assertEquals("Error", view.label!!.getCurrentText())
        assertEquals(view.label!!.params.errorTextColor, view.label!!.getCurrentColor())
    }

    fun createMatcher() = allOf(`is`(viewTestRule.view as View))!!
}