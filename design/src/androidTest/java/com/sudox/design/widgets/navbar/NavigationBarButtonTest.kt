package com.sudox.design.widgets.navbar

import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import android.view.ViewGroup
import com.novoda.espresso.ViewCreator
import com.novoda.espresso.ViewTestRule
import com.sudox.design.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class NavigationBarButtonTest {

    @Rule
    @JvmField
    val viewTestRule = ViewTestRule<NavigationBarButton>(ViewCreator<NavigationBarButton> { context, parentView ->
        return@ViewCreator NavigationBarButton(context).apply {
            text = "Назад"
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 90)
            isClickable = true
        }
    })

    @Test
    fun test() {
        viewTestRule.runOnMainSynchronously {
            it.setIconDrawableRes(R.drawable.ic_arrow_left)
            it.setIconDirection(NavigationBarButton.IconDirection.NAVIGATION_BAR_BUTTON_ICON_LEFT_DIRECTION)
            it.applyChanges()
        }

        Thread.sleep(5000)

        viewTestRule.runOnMainSynchronously {
            it.text = ""
        }

        Thread.sleep(500000)
    }
}