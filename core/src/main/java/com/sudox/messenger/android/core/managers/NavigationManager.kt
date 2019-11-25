package com.sudox.messenger.android.core.managers

import android.os.Bundle
import androidx.fragment.app.Fragment

interface NavigationManager {
    fun replaceFragment(fragment: Fragment, addToBackstack: Boolean)
    fun backToPreviousFragment()
    fun getCurrentFragment(): Fragment?
    fun restoreState(bundle: Bundle): Boolean
    fun saveState(bundle: Bundle)
    fun clearBackstack()
}