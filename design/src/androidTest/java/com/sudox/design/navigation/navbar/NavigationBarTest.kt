package com.sudox.design.navigation.navbar

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import com.novoda.espresso.ViewCreator
import com.novoda.espresso.ViewTestRule
import com.vanniktech.espresso.core.utils.TextViewDrawableMatcher.withNoTextViewDrawableLeft
import com.vanniktech.espresso.core.utils.TextViewDrawableMatcher.withNoTextViewDrawableRight
import org.hamcrest.CoreMatchers.*
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@LargeTest
@RunWith(AndroidJUnit4::class)
class NavigationBarTest : Assert() {

    @Rule
    @JvmField
    val viewTestRule = ViewTestRule<NavigationBar>(ViewCreator<NavigationBar> { context, parentView ->
        NavigationBar(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    })

    @Test
    fun testClickCallback() {
        val listener = Mockito.mock(NavigationBarListener::class.java)
        val navigationBar = viewTestRule.view

        viewTestRule.runOnMainSynchronously {
            navigationBar.listener = listener
            navigationBar.leftButton.setShowing(true)
            navigationBar.leftButton.isClickable = true
            navigationBar.leftButton.text = "Prev"
            navigationBar.rightButton.setShowing(true)
            navigationBar.rightButton.isClickable = true
            navigationBar.rightButton.text = "Next"
        }

        onView(`is`(navigationBar.leftButton)).perform(click())
        Mockito.verify(listener).onButtonClick(NavigationBarButton.Type.NAVIGATION_BAR_BUTTON_LEFT)
        Mockito.reset(listener)

        onView(`is`(navigationBar.rightButton)).perform(click())
        Mockito.verify(listener).onButtonClick(NavigationBarButton.Type.NAVIGATION_BAR_BUTTON_RIGHT)
    }

    @Test
    fun testResetButtonsView() {
        val listener = Mockito.mock(NavigationBarListener::class.java)
        val navigationBar = viewTestRule.view

        viewTestRule.runOnMainSynchronously {
            navigationBar.listener = listener
            navigationBar.leftButton.setShowing(true)
            navigationBar.leftButton.isClickable = true
            navigationBar.leftButton.text = "Prev"
            navigationBar.rightButton.setShowing(true)
            navigationBar.rightButton.isClickable = true
            navigationBar.rightButton.text = "Next"
            navigationBar.resetButtonsView()
        }

        onView(`is`(navigationBar.rightButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        viewTestRule.runOnMainSynchronously { navigationBar.rightButton.setShowing(true) }
        onView(`is`(navigationBar.rightButton)).check(matches(withNoTextViewDrawableLeft()))
        onView(`is`(navigationBar.rightButton)).check(matches(not(isClickable())))
        onView(`is`(navigationBar.rightButton)).check(matches(withText("")))

        onView(`is`(navigationBar.leftButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        viewTestRule.runOnMainSynchronously { navigationBar.leftButton.setShowing(true) }
        onView(`is`(navigationBar.leftButton)).check(matches(withNoTextViewDrawableRight()))
        onView(`is`(navigationBar.leftButton)).check(matches(not(isClickable())))
        onView(`is`(navigationBar.leftButton)).check(matches(withText("")))

        viewTestRule.runOnMainSynchronously {
            navigationBar.leftButton.text = "Prev"
            navigationBar.leftButton.isClickable = true
            navigationBar.rightButton.text = "Next"
            navigationBar.rightButton.isClickable = true
        }

        onView(`is`(navigationBar.leftButton)).perform(click())
        Mockito.verify(listener).onButtonClick(NavigationBarButton.Type.NAVIGATION_BAR_BUTTON_LEFT)
        Mockito.reset(listener)

        onView(`is`(navigationBar.rightButton)).perform(click())
        Mockito.verify(listener).onButtonClick(NavigationBarButton.Type.NAVIGATION_BAR_BUTTON_RIGHT)
    }

    @Test
    fun testAutoErrorResetting() {

    }

    fun createMatcher() = allOf(`is`(viewTestRule.view as View))!!
}