package com.sudox.design.outline

import android.graphics.Outline
import android.os.Build
import com.sudox.design.DesignTestRunner
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(DesignTestRunner::class)
class OutlineHelperTest : Assert() {

    @Test
    @Config(minSdk = Build.VERSION_CODES.LOLLIPOP, maxSdk = Build.VERSION_CODES.M)
    fun testGetRadiusOnPreNougat() {
        val outline = Outline().apply {
            setRoundRect(0, 0, 90, 90, 10F)
        }

        assertEquals(10F, outline.getBorderRadius())
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.N])
    fun testGetRadiusOnNougatDevices() {
        val outline = Outline().apply {
            setRoundRect(0, 0, 90, 90, 10F)
        }

        assertEquals(10F, outline.getBorderRadius())
    }
}