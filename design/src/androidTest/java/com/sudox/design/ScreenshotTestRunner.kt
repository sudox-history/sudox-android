package com.sudox.design

import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import com.facebook.testing.screenshot.ScreenshotRunner

@Suppress("unused")
class ScreenshotTestRunner : AndroidJUnitRunner() {

    override fun onCreate(args: Bundle?) {
        targetContext.setTheme(R.style.SudoxTheme)
        ScreenshotRunner.onCreate(this, args)
        super.onCreate(args)
    }

    override fun finish(resultCode: Int, results: Bundle?) {
        ScreenshotRunner.onDestroy()
        super.finish(resultCode, results)
    }
}