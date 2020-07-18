package ru.sudox.android.core.ui.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.util.*

fun popBackstack(fragmentManager: FragmentManager): Boolean {
    val managers = Stack<FragmentManager>().apply { push(fragmentManager) }
    val visibleFragments = Stack<Fragment>()
    var lastVisibleContainer: Fragment? = null

    while (managers.isNotEmpty()) {
        managers.pop().fragments.forEach {
            if (it.isVisible) {
                if (it is ContainerFragment) {
                    lastVisibleContainer = it
                }

                managers.push(it.childFragmentManager)
                visibleFragments.push(it)
            }
        }
    }

    if (lastVisibleContainer != null) {
        if ((lastVisibleContainer as ContainerFragment).onBackPressed()) {
            return true
        }
    }

    var hostFragmentManager: FragmentManager? = null

    while (visibleFragments.isNotEmpty()) {
        val fragment = visibleFragments.pop()

        if (fragment!!.parentFragment == null) {
            break
        } else if (fragment.parentFragmentManager.backStackEntryCount > 0) {
            hostFragmentManager = fragment.parentFragmentManager
            break
        }
    }

    if (hostFragmentManager != null) {
        hostFragmentManager.popBackStack()
        return true
    } else if (fragmentManager.backStackEntryCount > 0) {
        fragmentManager.popBackStack()
        return true
    }

    return false
}