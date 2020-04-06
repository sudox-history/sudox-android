package ru.sudox.android.managers

import android.app.Activity
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED
import ru.sudox.android.core.managers.ScreenManager

class AppScreenManager(val activity: Activity) : ScreenManager {

    override fun setInputMode(mode: Int) {
        activity.let {
            if (it.window.attributes.softInputMode != mode) {
                it.window.setSoftInputMode(mode)
            }
        }
    }

    override fun setOrientation(orientation: Int) {
        activity.let {
            if (it.window.attributes.screenOrientation != orientation) {
                it.requestedOrientation = orientation
            }
        }
    }

    override fun reset() {
        setInputMode(SOFT_INPUT_STATE_UNCHANGED)
        setOrientation(SCREEN_ORIENTATION_USER)
    }
}