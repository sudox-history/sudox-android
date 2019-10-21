package com.sudox.messenger.android.core.managers

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sudox.design.applicationBar.ApplicationBarListener

interface ApplicationBarManager {
    fun setTitle(@StringRes titleTextId: Int)
    fun setListener(listener: ApplicationBarListener)
    fun showButtonAtStart(@DrawableRes iconDrawableId: Int)
    fun showButtonAtEnd(@DrawableRes iconDrawableId: Int)
    fun showBackButton()
    fun reset()
}