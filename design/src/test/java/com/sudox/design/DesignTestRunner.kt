package com.sudox.design

import android.os.Build
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

class DesignTestRunner(testClass: Class<*>) : RobolectricTestRunner(testClass) {

    override fun buildGlobalConfig(): Config {
        return Config.Builder()
                .setApplication(DesignTestApplication::class.java)
                .setPackageName("com.sudox.design")
                .setSdk(Build.VERSION_CODES.LOLLIPOP)
                .build()
    }
}