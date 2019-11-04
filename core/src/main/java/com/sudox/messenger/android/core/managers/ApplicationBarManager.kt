package com.sudox.messenger.android.core.managers

import androidx.annotation.StringRes
import com.sudox.design.applicationBar.APPBAR_FIRST_END_BUTTON_TAG
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.design.applicationBar.applicationBarButton.ApplicationBarButton

const val APPBAR_NEXT_BUTTON_INDEX = 0
const val APPBAR_NEXT_BUTTON_TAG = APPBAR_FIRST_END_BUTTON_TAG

interface ApplicationBarManager {
    fun getButtonStart(): ApplicationBarButton
    fun getButtonNext(): ApplicationBarButton
    fun toggleButtonBack(toggle: Boolean)
    fun toggleButtonNext(toggle: Boolean)
    fun setListener(listener: ApplicationBarListener?)
    fun setTitleText(@StringRes titleId: Int)
    fun reset()
}