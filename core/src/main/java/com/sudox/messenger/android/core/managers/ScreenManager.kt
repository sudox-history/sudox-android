package com.sudox.messenger.android.core.managers

interface ScreenManager {
    fun setInputMode(mode: Int)
    fun setOrientation(orientation: Int)
    fun reset()
}