package ru.sudox.android.core.managers

import android.os.Bundle
import com.bluelinelabs.conductor.Controller

const val AUTH_ROOT_TAG = 0
const val MAIN_ROOT_TAG = 1

interface NewNavigationManager {
    fun popBackstack(): Boolean
    fun showRootChild(controller: Controller)
    fun showSubRoot(controller: Controller)
    fun restoreState(bundle: Bundle?)
    fun saveState(bundle: Bundle?)
    fun showRoot(tag: Int)
}