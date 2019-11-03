package com.sudox.messenger.android.core.managers

import androidx.annotation.StringRes
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.design.applicationBar.applicationBarButton.ApplicationBarButton

interface ApplicationBarManager {
    fun getButtonStart(): ApplicationBarButton
    fun getButtonNext(): ApplicationBarButton
    fun toggleButtonBack(toggle: Boolean)
    fun toggleButtonNext(toggle: Boolean)
    fun setListener(listener: ApplicationBarListener?)
    fun setTitleText(@StringRes titleId: Int)
    fun reset()
}