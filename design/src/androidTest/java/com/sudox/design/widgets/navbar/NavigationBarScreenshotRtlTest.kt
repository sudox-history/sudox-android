package com.sudox.design.widgets.navbar

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.test.platform.app.InstrumentationRegistry
import com.facebook.testing.screenshot.Screenshot
import com.facebook.testing.screenshot.ViewHelpers
import com.sudox.design.R
import com.sudox.design.rules.ForceLocaleRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

private const val TITLE_TEXT = "عنوان"

class NavigationBarScreenshotRtlTest : Assert() {

    @Rule
    @JvmField
    val localeRule: ForceLocaleRule = ForceLocaleRule(Locale.forLanguageTag("ar-EG"))

    private lateinit var context: Context
    private lateinit var navigationBar: NavigationBar

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        navigationBar = NavigationBar(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)

            id = 1
        }
    }

    @Test
    fun testButtonsPositioning() {
        navigationBar.buttonStart!!.visibility = View.VISIBLE
        navigationBar.buttonStart!!.setIconDrawableRes(R.drawable.abc_vector_test)

        navigationBar.buttonsEnd[0]!!.visibility = View.VISIBLE
        navigationBar.buttonsEnd[0]!!.setText("أول")
        navigationBar.buttonsEnd[1]!!.visibility = View.VISIBLE
        navigationBar.buttonsEnd[1]!!.setText("ثانيا")
        navigationBar.buttonsEnd[2]!!.visibility = View.VISIBLE
        navigationBar.buttonsEnd[2]!!.setText("الثالث")

        ViewHelpers
                .setupView(navigationBar)
                .setExactWidthDp(600)
                .setExactHeightDp(60)
                .layout()

        Screenshot.snap(navigationBar)!!.record()
    }

    @Test
    fun testAllButtonsPositioningWithTitle() {
        navigationBar.setTitleText(TITLE_TEXT)
        navigationBar.buttonStart!!.visibility = View.VISIBLE
        navigationBar.buttonStart!!.setIconDrawableRes(R.drawable.abc_vector_test)

        navigationBar.buttonsEnd[0]!!.visibility = View.VISIBLE
        navigationBar.buttonsEnd[0]!!.setText("أول")
        navigationBar.buttonsEnd[1]!!.visibility = View.VISIBLE
        navigationBar.buttonsEnd[1]!!.setText("ثانيا")
        navigationBar.buttonsEnd[2]!!.visibility = View.VISIBLE
        navigationBar.buttonsEnd[2]!!.setText("الثالث")

        ViewHelpers
                .setupView(navigationBar)
                .setExactWidthDp(600)
                .setExactHeightDp(60)
                .layout()

        Screenshot.snap(navigationBar)!!.record()
    }

    @Test
    fun testTitlePositioning() {
        navigationBar.setTitleText(TITLE_TEXT)

        ViewHelpers
                .setupView(navigationBar)
                .setExactWidthDp(600)
                .setExactHeightDp(60)
                .layout()

        Screenshot.snap(navigationBar)!!.record()
    }

    @Test
    fun testTitlePositioningWithButtonStart() {
        navigationBar.setTitleText(TITLE_TEXT)
        navigationBar.buttonStart!!.visibility = View.VISIBLE
        navigationBar.buttonStart!!.setIconDrawableRes(R.drawable.abc_vector_test)

        ViewHelpers
                .setupView(navigationBar)
                .setExactWidthDp(600)
                .setExactHeightDp(60)
                .layout()

        Screenshot.snap(navigationBar)!!.record()
    }

    @Test
    fun testTitlePositioningWithButtonEnd() {
        navigationBar.setTitleText(TITLE_TEXT)

        navigationBar.buttonsEnd[0]!!.visibility = View.VISIBLE
        navigationBar.buttonsEnd[0]!!.setIconDrawableRes(R.drawable.abc_ic_clear_material)

        ViewHelpers
                .setupView(navigationBar)
                .setExactWidthDp(600)
                .setExactHeightDp(60)
                .layout()

        Screenshot.snap(navigationBar)!!.record()
    }
}