package com.sudox.messenger.android.core.controller

interface AppNavbarController {
    fun getTagButtonNext(): Int
    fun toggleButtonBack(toggle: Boolean)
    fun toggleButtonNext(toggle: Boolean)
    fun setButtonsClickCallback(callback: (Int) -> (Unit))
    fun reset()
}