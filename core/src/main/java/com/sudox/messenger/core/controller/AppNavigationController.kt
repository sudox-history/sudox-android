package com.sudox.messenger.core.controller

import android.os.Bundle
import com.sudox.messenger.core.fragment.AppFragment

interface AppNavigationController {
    fun popBackstack()
    fun openFragment(fragment: AppFragment)
    fun getCurrentFragment(): AppFragment
    fun clearBackstack()
    fun saveFragmentToState(outState: Bundle)
    fun restoreFragmentFromState(savedInstanceState: Bundle)
    fun canRestoreFragmentFromState(savedInstanceState: Bundle?): Boolean
}