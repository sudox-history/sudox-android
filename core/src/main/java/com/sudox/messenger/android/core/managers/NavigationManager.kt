package com.sudox.messenger.android.core.managers

import android.os.Bundle
import androidx.fragment.app.Fragment

interface NavigationManager {
    fun showAuthPart()
    fun showMainPart()
    fun popBackstack()
    fun addFragment(fragment: Fragment)
    fun restoreState(bundle: Bundle): Boolean
    fun saveState(bundle: Bundle)
}