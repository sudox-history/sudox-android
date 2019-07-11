package com.sudox.design.widgets.etlayout

import android.content.Context
import android.view.ViewGroup
import android.widget.EditText
import androidx.test.espresso.Espresso.onView
import androidx.test.platform.app.InstrumentationRegistry
import com.facebook.testing.screenshot.Screenshot
import com.facebook.testing.screenshot.ViewHelpers
import com.sudox.design.rules.ForceLocaleRule
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

private const val HINT_TEXT = "ملحوظة"
private const val ERROR_TEXT = "خطأ"

class EditTextLayoutScreenshotRtlTest : Assert() {

    @Rule
    @JvmField
    val localeRule: ForceLocaleRule = ForceLocaleRule(Locale.forLanguageTag("ar-EG"))

    private lateinit var context: Context
    private lateinit var editTextLayout: EditTextLayout
    private lateinit var editText: EditText

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        editText = EditText(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)

            hint = HINT_TEXT
            id = 1
        }

        editTextLayout = EditTextLayout(context).apply {
            addView(this@EditTextLayoutScreenshotRtlTest.editText)
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)

            id = 2
        }

        ViewHelpers
                .setupView(editTextLayout)
                .setExactWidthDp(300)
                .layout()
    }

    @Test
    fun testIdleStateRendering() {
        Screenshot.snap(editTextLayout).record()
    }

    @Test
    fun testErrorRendering() {
        editTextLayout.setErrorText(ERROR_TEXT)
        Screenshot.snap(editTextLayout).record()
    }

    @Test
    fun testFocusedStateRendering() {
        editTextLayout.requestFocus()
        Screenshot.snap(editTextLayout).record()
    }

    @Test
    fun testDisableStateRendering() {
        editTextLayout.isEnabled = false
        Screenshot.snap(editTextLayout).record()
    }
}