package com.sudox.messenger.android.managers

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sudox.design.navigationBar.NavigationBar
import com.sudox.messenger.android.R
import com.sudox.messenger.android.core.managers.NavigationManager

internal const val CURRENT_FRAGMENT_TAG = "current_fragment_tag"
internal const val CURRENT_FRAGMENT_KEY = "current_fragment_key"

class AppNavigationManager(
        val activity: Activity,
        val fragmentManager: FragmentManager,
        val navigationBar: NavigationBar,
        val containerId: Int
) : NavigationManager {

    override fun replaceFragment(fragment: Fragment, addToBackstack: Boolean) {
        val transaction = fragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.animator_fragment_enter_push,
                        R.animator.animator_fragment_exit_push,
                        R.animator.animator_fragment_enter_pop,
                        R.animator.animator_fragment_exit_pop
                )
                .replace(containerId, fragment, CURRENT_FRAGMENT_TAG)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }

    override fun getCurrentFragment(): Fragment? {
        return fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG)
    }

    override fun backToPreviousFragment() {
        activity.onKeyDown(KeyEvent.KEYCODE_BACK, null)
    }

    override fun restoreState(bundle: Bundle): Boolean {
        fragmentManager.getFragment(bundle, CURRENT_FRAGMENT_KEY)?.let {
            replaceFragment(it, false)
        }

        return true
    }

    override fun saveState(bundle: Bundle) {
        getCurrentFragment()?.let {
            fragmentManager.putFragment(bundle, CURRENT_FRAGMENT_KEY, it)
        }
    }

    override fun clearBackstack() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}