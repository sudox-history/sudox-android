package com.sudox.messenger.android.moments

import android.os.Build
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

class MomentsTestRunner(testClass: Class<*>) : RobolectricTestRunner(testClass) {

    override fun buildGlobalConfig(): Config {
        return Config.Builder()
                .setApplication(MomentsTestApplication::class.java)
                .setPackageName("com.sudox.messenger.android.moments")
                .setSdk(Build.VERSION_CODES.LOLLIPOP)
                .build()
    }
}