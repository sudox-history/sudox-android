package com.sudox.messenger.android

import android.os.Build
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

class TestRunner(testClass: Class<*>) : RobolectricTestRunner(testClass) {

    override fun buildGlobalConfig(): Config {
        return Config.Builder()
                .setApplication(TestApplication::class.java)
                .setPackageName("com.sudox.messenger.android")
                .setSdk(Build.VERSION_CODES.LOLLIPOP)
                .build()
    }
}