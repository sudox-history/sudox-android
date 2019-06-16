package com.sudox.messenger.impls

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.sudox.messenger.core.fragment.AppFragment
import com.sudox.messenger.core.controller.AppNavigationController

private const val FRAGMENT_TAG = "fragmentTag"
private const val FRAGMENT_KEY = "fragment"

class AppNavigationControllerImpl(val containerId: Int, val fragmentManager: FragmentManager)
    : AppNavigationController {

    override fun popBackstack(params: Bundle?, restoreState: Boolean) {
        val fragments = fragmentManager.fragments
        val previousFragment = fragments[fragments.size - 2] as AppFragment

        if (restoreState) {
            previousFragment.setInitialSavedState(null)
        }

        if (params != null) {
            previousFragment.params = params
        }

        fragmentManager.popBackStack()
    }

    override fun openFragment(fragment: AppFragment) {
        fragmentManager
                .beginTransaction()
                .replace(containerId, fragment, FRAGMENT_TAG)
                .addToBackStack(null)
                .commit()
    }

    override fun getCurrentFragment(): AppFragment {
        return fragmentManager.findFragmentByTag(FRAGMENT_TAG) as AppFragment
    }

    override fun clearBackstack() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun saveFragmentToState(outState: Bundle) {
        val fragment = getCurrentFragment()
        fragmentManager.putFragment(outState, FRAGMENT_KEY, fragment)
    }

    override fun restoreFragmentFromState(savedInstanceState: Bundle) {
        val fragment = fragmentManager.getFragment(savedInstanceState, FRAGMENT_KEY) as AppFragment
        openFragment(fragment)
    }

    override fun canRestoreFragmentFromState(savedInstanceState: Bundle?): Boolean {
        return savedInstanceState != null && savedInstanceState.containsKey(FRAGMENT_KEY)
    }
}