package com.sudox.design.widgets.navbar.button

import android.content.Context
import android.view.ViewGroup
import androidx.test.platform.app.InstrumentationRegistry
import com.facebook.testing.screenshot.Screenshot
import com.facebook.testing.screenshot.ViewHelpers
import com.sudox.design.R
import com.sudox.design.rules.ForceLocaleRule
import com.sudox.design.widgets.navbar.NavigationBar
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

private const val BUTTON_TEXT = "Text"

class NavigationBarButtonLtrTest {

    @Rule
    @JvmField
    val localeRule: ForceLocaleRule = ForceLocaleRule(Locale.forLanguageTag("ru-RU"))

    private lateinit var context: Context
    private lateinit var navigationBar: NavigationBar
    private lateinit var navigationBarButton: NavigationBarButton

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

        // Used only for button params loading
        navigationBar = NavigationBar(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)

            id = 1
        }

        navigationBarButton = NavigationBarButton(context, navigationBar.buttonParams).apply {
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)

            id = 2
        }
    }

    @Test
    fun testIconAtEndPositioning() {
        navigationBarButton.setIconDrawableRes(R.drawable.abc_ic_clear_material)
        navigationBarButton.setIconDirection(NavigationBarButtonIconDirection.END)
        navigationBarButton.setText(BUTTON_TEXT)

        ViewHelpers
                .setupView(navigationBarButton)
                .setExactWidthDp(300)
                .setExactHeightDp(60)
                .layout()

        Screenshot.snap(navigationBarButton)!!.record()
    }

    @Test
    fun testIconAtStartPositioning() {
        navigationBarButton.setIconDrawableRes(R.drawable.abc_ic_clear_material)
        navigationBarButton.setIconDirection(NavigationBarButtonIconDirection.START)
        navigationBarButton.setText(BUTTON_TEXT)

        ViewHelpers
                .setupView(navigationBarButton)
                .setExactWidthDp(300)
                .setExactHeightDp(60)
                .layout()

        Screenshot.snap(navigationBarButton)!!.record()
    }

    @Test
    fun testOnlyIconPositioning() {
        navigationBarButton.setIconDrawableRes(R.drawable.abc_ic_clear_material)

        ViewHelpers
                .setupView(navigationBarButton)
                .setExactWidthDp(300)
                .setExactHeightDp(60)
                .layout()

        Screenshot.snap(navigationBarButton)!!.record()
    }

    @Test
    fun testOnlyTextPositioning() {
        navigationBarButton.setText(BUTTON_TEXT)

        ViewHelpers
                .setupView(navigationBarButton)
                .setExactWidthDp(300)
                .setExactHeightDp(60)
                .layout()

        Screenshot.snap(navigationBarButton)!!.record()
    }
}