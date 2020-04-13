package ru.sudox.android.core.managers

import com.bluelinelabs.conductor.Controller

const val AUTH_ROOT_TAG = 0
const val PEOPLE_ROOT_TAG = 1
const val DIALOGS_ROOT_TAG = 2
const val PROFILE_ROOT_TAG = 3

interface NewNavigationManager {
    fun clearBackstack()
    fun popBackstack(): Boolean
    fun showChild(controller: Controller)
    fun showRoot(tag: Int)
}