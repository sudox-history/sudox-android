package com.sudox.design.navigation.navbar

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import com.novoda.espresso.ViewCreator
import com.novoda.espresso.ViewTestRule
import com.sudox.design.R
import com.vanniktech.espresso.core.utils.TextViewDrawableMatcher.*
import org.hamcrest.CoreMatchers.*
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class NavigationBarRightButtonTest : Assert() {

    @Rule
    @JvmField
    val viewTestRule = ViewTestRule<NavigationBarButton>(ViewCreator<NavigationBarButton> { context, parentView ->
        NavigationBarButton(context, NavigationBarButton.Type.NAVIGATION_BAR_BUTTON_RIGHT).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    })

    @Test
    fun testOnStartup() {
        onView(createMatcher()).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(createMatcher()).check(matches(not(withTextViewDrawableRight(R.drawable.ic_arrow_right))))
        onView(createMatcher()).check(matches(not(isClickable())))
        onView(createMatcher()).check(matches(withText("")))
    }

    @Test
    fun testSetShowing() {
        val view = viewTestRule.view

        viewTestRule.runOnMainSynchronously { view.setShowing(true) }
        onView(createMatcher()).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(createMatcher()).check(matches(withNoTextViewDrawableRight()))

        viewTestRule.runOnMainSynchronously { view.setShowing(false) }
        onView(createMatcher()).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(createMatcher()).check(matches(not(withTextViewDrawableRight(R.drawable.ic_arrow_right))))
    }

    @Test
    fun testSetArrowShowing() {
        val view = viewTestRule.view

        viewTestRule.runOnMainSynchronously { view.setShowing(true) }
        viewTestRule.runOnMainSynchronously { view.setArrowShowing(true) }
        onView(createMatcher()).check(matches(withTextViewDrawableRight(R.drawable.ic_arrow_right)))

        viewTestRule.runOnMainSynchronously { view.setArrowShowing(false) }
        onView(createMatcher()).check(matches(withNoTextViewDrawableRight()))
    }

    @Test
    fun testResetView() {
        val view = viewTestRule.view

        viewTestRule.runOnMainSynchronously { view.setShowing(true) }
        viewTestRule.runOnMainSynchronously { view.setArrowShowing(true) }
        viewTestRule.runOnMainSynchronously { view.isClickable = true }
        viewTestRule.runOnMainSynchronously { view.text = "Text" }
        viewTestRule.runOnMainSynchronously { view.resetView() }

        onView(createMatcher()).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        viewTestRule.runOnMainSynchronously { view.setShowing(true) }
        onView(createMatcher()).check(matches(withNoTextViewDrawableRight()))
        onView(createMatcher()).check(matches(not(isClickable())))
        onView(createMatcher()).check(matches(withText("")))
    }

    fun createMatcher() = allOf(`is`(viewTestRule.view as View))!!
}