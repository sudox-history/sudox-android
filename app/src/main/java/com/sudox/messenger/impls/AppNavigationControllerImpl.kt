package com.sudox.messenger.impls

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.sudox.messenger.core.controller.AppNavigationController
import com.sudox.messenger.core.fragment.AppFragment

internal const val CURRENT_FRAGMENT_TAG = "current_fragment_tag"
internal const val CURRENT_FRAGMENT_KEY = "current_fragment_key"

class AppNavigationControllerImpl(
    val containerId: Int,
    val fragmentManager: FragmentManager
) : AppNavigationController {

    override fun showPreviousFragment(): Boolean {
        return fragmentManager.popBackStackImmediate()
    }

    override fun showFragment(fragment: AppFragment, addToBackstack: Boolean) {
        val transaction = fragmentManager
                .beginTransaction()
                .replace(containerId, fragment, CURRENT_FRAGMENT_TAG)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }

    override fun getCurrentFragment(): AppFragment {
        return fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG) as AppFragment
    }

    override fun saveState(bundle: Bundle) {
        val currentFragment = getCurrentFragment()
        fragmentManager.putFragment(bundle, CURRENT_FRAGMENT_KEY, currentFragment)
    }

    override fun restoreState(bundle: Bundle?): Boolean {
        if (bundle == null) {
            return false
        }

        val fragment = fragmentManager.getFragment(bundle, CURRENT_FRAGMENT_KEY) as AppFragment
        showFragment(fragment, false)
        return true
    }

    override fun clearBackstack() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}