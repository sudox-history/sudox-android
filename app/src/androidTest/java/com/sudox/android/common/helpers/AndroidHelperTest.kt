package com.sudox.android.common.helpers

import android.content.Intent
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.runner.AndroidJUnit4
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.protocol.helpers.randomBase64String
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class AndroidHelperTest : Assert() {

    private val intentsTestRule: IntentsTestRule<AuthActivity> = IntentsTestRule(AuthActivity::class.java)

    @Test
    fun testSendMessageViaSms() {
        val message = randomBase64String(Random.nextInt(10))
        val phone = Random.nextInt(100000, 10000000).toString()

        // Testing
        intentsTestRule.launchActivity(Intent())
        intentsTestRule.activity.sendMessageViaSms(phone, message)

        // Verifying
        Intents.intended(Matchers.allOf(
                Matchers.anyOf(
                        IntentMatchers.toPackage("com.android.mms"),
                        IntentMatchers.toPackage("com.google.android.apps.messaging"),
                        IntentMatchers.toPackage("com.android.messaging")
                )
        ))

        // End this test
        intentsTestRule.finishActivity()
    }
}