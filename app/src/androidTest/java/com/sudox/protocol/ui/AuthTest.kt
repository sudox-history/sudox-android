package com.sudox.protocol.ui

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.sudox.android.R
import com.sudox.android.ui.splash.SplashActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AuthTest {

    @Rule
    @JvmField
    val splashActivityRule = ActivityTestRule(SplashActivity::class.java, true, false)

    @Test
    fun authTest() {
        val intent = Intent()
        intent.putExtra("EXTRA", "Test")
        splashActivityRule.launchActivity(intent)
        onView(withId(R.id.emailEditText)).perform(typeText("kerjen01@gmail.com"))
        onView(withId(R.id.buttonNavbarNext)).perform(click())
    }
}