package com.sudox.messenger.android.core.managers

import android.os.Bundle
import androidx.fragment.app.Fragment

interface NavigationManager {
    fun showPreviousFragment(): Boolean
    fun showFragment(fragment: Fragment, addToBackstack: Boolean)
    fun getCurrentFragment(): Fragment?
    fun restoreState(bundle: Bundle): Boolean
    fun saveState(bundle: Bundle)
    fun clearBackstack()
}