package com.sudox.messenger.android.managers

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sudox.design.navigationBar.NavigationBar
import com.sudox.design.navigationBar.NavigationBarListener
import com.sudox.messenger.android.R
import com.sudox.messenger.android.core.managers.NavigationManager

internal const val PEOPLE_NAVBAR_ITEM_ID = 1
internal const val MESSAGES_NAVBAR_ITEM_ID = 2
internal const val WORLD_NAVBAR_ITEM_ID = 3
internal const val PROFILE_NAVBAR_ITEM_ID = 4

internal val backStacksNames = hashMapOf(
        PEOPLE_NAVBAR_ITEM_ID to "people_backstack",
        MESSAGES_NAVBAR_ITEM_ID to "messages_backstack",
        WORLD_NAVBAR_ITEM_ID to "world_backstack",
        PROFILE_NAVBAR_ITEM_ID to "profile_backstack"
)

class AppNavigationManager(
        val activity: Activity,
        val fragmentManager: FragmentManager,
        val navigationBar: NavigationBar,
        val containerId: Int
) : NavigationManager, NavigationBarListener {

    private var navbarConfigured = false
    private var currentItemTag = 0

    fun configureNavbar() {
        if (!navbarConfigured) {
            navigationBar.let {
                it.addItem(PEOPLE_NAVBAR_ITEM_ID, R.string.people, R.drawable.ic_group)
                it.addItem(MESSAGES_NAVBAR_ITEM_ID, R.string.messages, R.drawable.ic_chat_bubble)
                it.addItem(WORLD_NAVBAR_ITEM_ID, R.string.world, R.drawable.ic_album)
                it.addItem(PROFILE_NAVBAR_ITEM_ID, R.string.profile, R.drawable.ic_account)
                it.listener = this
            }

            navbarConfigured = true
        }
    }

    override fun showAuthPart() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMainPart() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun popBackstack() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addFragment(fragment: Fragment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun restoreState(bundle: Bundle): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveState(bundle: Bundle) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onButtonClicked(tag: Int) {
        currentItemTag = tag
    }
}