package com.sudox.messenger.android.core.controller

import android.os.Bundle
import com.sudox.messenger.android.core.fragment.AppFragment

interface AppNavigationController {
    fun showPreviousFragment(): Boolean
    fun showFragment(fragment: AppFragment, addToBackstack: Boolean = true)
    fun getCurrentFragment(): AppFragment
    fun saveState(bundle: Bundle)
    fun restoreState(bundle: Bundle?): Boolean
    fun clearBackstack()
}