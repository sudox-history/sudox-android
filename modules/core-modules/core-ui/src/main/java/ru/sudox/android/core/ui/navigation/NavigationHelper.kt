package ru.sudox.android.core.ui.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

fun BottomNavigationView.setupWithFragmentManager(
    backstack: Stack<Int>,
    @IdRes containerId: Int,
    fragmentManager: FragmentManager,
    createFragment: (Int) -> (Fragment)
) {
    if (fragmentManager.fragments.isEmpty()) {
        fragmentManager
            .beginTransaction()
            .add(containerId, createFragment(selectedItemId), selectedItemId.toString())
            .commit()
    }

    setOnNavigationItemSelectedListener {
        val previousItemId = selectedItemId
        val selectedItemId = it.itemId

        if (previousItemId == selectedItemId) {
            // TODO: State resetting
            return@setOnNavigationItemSelectedListener true
        }

        val transaction = fragmentManager.beginTransaction()
        var selectedFragment = fragmentManager.findFragmentByTag(selectedItemId.toString())
        val backstackIterator = backstack.iterator()

        if (backstack.size > 0 && selectedItemId != backstack[0]) {
            while (backstackIterator.hasNext()) {
                if (backstackIterator.next() == selectedItemId) {
                    backstackIterator.remove()
                }
            }
        }

        backstack.push(previousItemId)

        if (selectedFragment == null) {
            selectedFragment = createFragment(selectedItemId)
            transaction.add(containerId, selectedFragment, selectedItemId.toString())
        } else {
            transaction.attach(selectedFragment)
        }

        transaction
            .detach(fragmentManager.findFragmentByTag(previousItemId.toString())!!)
            .commit()

        true
    }
}

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