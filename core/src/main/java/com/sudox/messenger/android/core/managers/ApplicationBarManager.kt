package com.sudox.messenger.android.core.managers

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sudox.design.applicationBar.APPBAR_FIRST_END_BUTTON_TAG
import com.sudox.design.applicationBar.ApplicationBarListener

const val APPBAR_NEXT_BUTTON_INDEX = 0
const val APPBAR_NEXT_BUTTON_TAG = APPBAR_FIRST_END_BUTTON_TAG

interface ApplicationBarManager {
    fun toggleTextButtonAtStart(@StringRes titleId: Int)
    fun toggleIconButtonAtStart(@DrawableRes iconId: Int)
    fun toggleTextButtonAtEnd(@StringRes titleId: Int)
    fun toggleIconButtonAtEnd(@DrawableRes iconId: Int)
    fun toggleButtonBack(toggle: Boolean)
    fun toggleButtonNext(toggle: Boolean)
    fun setListener(listener: ApplicationBarListener?)
    fun setTitleText(@StringRes titleId: Int)
    fun setTitleText(text: String)
    fun setContentView(view: View?)
    fun reset(resetTitle: Boolean = true)
}