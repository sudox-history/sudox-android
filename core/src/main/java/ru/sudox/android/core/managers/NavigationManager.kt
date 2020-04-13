package ru.sudox.android.core.managers

import android.os.Bundle
import com.bluelinelabs.conductor.Controller

const val AUTH_ROOT_TAG = 0
const val PEOPLE_ROOT_TAG = 1
const val DIALOGS_ROOT_TAG = 2
const val PROFILE_ROOT_TAG = 3

interface NewNavigationManager {
    fun clearBackstack()
    fun popBackstack(): Boolean
    fun restoreState(bundle: Bundle?)
    fun saveState(bundle: Bundle?)
    fun showChild(controller: Controller)
    fun showRoot(tag: Int)
}