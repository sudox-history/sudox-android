package com.sudox.messenger.android.core.managers

import android.os.Bundle
import androidx.fragment.app.Fragment

interface NavigationManager {
    fun showAuthPart()
    fun showMainPart()
    fun configureNavigationBar()
    fun showChildFragment(fragment: Fragment)
    fun popBackstack(): Boolean
    fun restoreState(bundle: Bundle): Boolean
    fun saveState(bundle: Bundle)
}