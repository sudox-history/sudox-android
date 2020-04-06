package ru.sudox.android.core.managers

@Deprecated(message = "Будет заменен вызовом VO")
interface ScreenManager {
    fun setInputMode(mode: Int)
    fun setOrientation(orientation: Int)
    fun reset()
}