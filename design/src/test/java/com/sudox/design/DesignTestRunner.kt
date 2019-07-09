package com.sudox.design

import android.os.Build
import com.sudox.design.shadows.LayoutDirectionLinearLayoutDirection
import com.sudox.design.shadows.LayoutDirectionTextViewShadow
import com.sudox.design.shadows.LayoutDirectionViewGroupShadow
import com.sudox.design.shadows.LayoutDirectionViewShadow
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

class DesignTestRunner(testClass: Class<*>) : RobolectricTestRunner(testClass) {

    override fun buildGlobalConfig(): Config {
        return Config.Builder()
                .setApplication(DesignTestApplication::class.java)
                .setPackageName("com.sudox.design")
                .setSdk(Build.VERSION_CODES.LOLLIPOP)
                .setShadows(
                        LayoutDirectionViewShadow::class.java,
                        LayoutDirectionTextViewShadow::class.java,
                        LayoutDirectionViewGroupShadow::class.java,
                        LayoutDirectionLinearLayoutDirection::class.java)
                .build()
    }
}