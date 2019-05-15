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
import com.vanniktech.espresso.core.utils.TextViewDrawableMatcher.withNoTextViewDrawableLeft
import com.vanniktech.espresso.core.utils.TextViewDrawableMatcher.withTextViewDrawableLeft
import org.hamcrest.CoreMatchers.*
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class NavigationBarLeftButtonTest : Assert() {

    @Rule
    @JvmField
    val viewTestRule = ViewTestRule<NavigationBarButton>(ViewCreator<NavigationBarButton> { context, parentView ->
        NavigationBarButton(context, NavigationBarButton.Type.NAVIGATION_BAR_BUTTON_LEFT).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    })

    @Test
    fun testOnStartup() {
        onView(createMatcher()).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(createMatcher()).check(matches(not(withTextViewDrawableLeft(R.drawable.ic_arrow_left))))
        onView(createMatcher()).check(matches(not(isClickable())))
        onView(createMatcher()).check(matches(withText("")))
    }

    @Test
    fun testSetShowing() {
        val view = viewTestRule.view

        viewTestRule.runOnMainSynchronously { view.setShowing(true) }
        onView(createMatcher()).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(createMatcher()).check(matches(withNoTextViewDrawableLeft()))

        viewTestRule.runOnMainSynchronously { view.setShowing(false) }
        onView(createMatcher()).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(createMatcher()).check(matches(not(withTextViewDrawableLeft(R.drawable.ic_arrow_left))))
    }

    @Test
    fun testSetArrowShowing() {
        val view = viewTestRule.view

        viewTestRule.runOnMainSynchronously { view.setShowing(true) }
        viewTestRule.runOnMainSynchronously { view.setArrowShowing(true) }
        onView(createMatcher()).check(matches(withTextViewDrawableLeft(R.drawable.ic_arrow_left)))

        viewTestRule.runOnMainSynchronously { view.setArrowShowing(false) }
        onView(createMatcher()).check(matches(withNoTextViewDrawableLeft()))
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
        onView(createMatcher()).check(matches(withNoTextViewDrawableLeft()))
        onView(createMatcher()).check(matches(not(isClickable())))
        onView(createMatcher()).check(matches(withText("")))
    }

    fun createMatcher() = allOf(`is`(viewTestRule.view as View))!!
}