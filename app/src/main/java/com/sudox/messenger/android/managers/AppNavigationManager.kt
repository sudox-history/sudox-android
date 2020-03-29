package com.sudox.messenger.android.managers

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sudox.messenger.android.R
import com.sudox.messenger.android.auth.phone.AuthPhoneFragment
import com.sudox.messenger.android.core.managers.NavigationManager
import com.sudox.messenger.android.messages.DialogsFragment
import com.sudox.messenger.android.people.PeopleFragment
import com.sudox.messenger.android.people.ProfileFragment
import java.util.LinkedList
import java.util.UUID

internal const val PEOPLE_NAVBAR_ITEM_ID = 1
internal const val DIALOGS_NAVBAR_ITEM_ID = 2
internal const val PROFILE_NAVBAR_ITEM_ID = 3

internal const val NAVIGATION_BAR_VISIBLE_EXTRA_KEY = "navigation_bar_visible"
internal const val BACKSTACK_SIZE_EXTRA_KEY = "backstack_size"
internal const val BACKSTACK_ITEM_TAG_AT_INDEX_EXTRA_KEY = "backstack_item_tag_at_index"
internal const val BACKSTACK_FRAGMENT_TAG_AT_INDEX_EXTRA_KEY = "backstack_fragment_tag_at_index"
internal const val LOADED_FRAGMENTS_COUNT_EXTRA_KEY = "loaded_fragments_count"
internal const val LOADED_FRAGMENT_ITEM_TAG_AT_INDEX_EXTRA_KEY = "loaded_fragment_item_tag_at_index"
internal const val LOADED_FRAGMENT_TAG_AT_INDEX_EXTRA_KEY = "loaded_fragment_fragment_tag_at_index"
internal const val CURRENT_FRAGMENT_TAG_EXTRA_KEY = "current_fragment_tag"
internal const val CURRENT_ITEM_TAG_EXTRA_KEY = "current_item_tag"

/**
 * Менеджер навигации данного приложения.
 *
 * Реализована навигация по следующему механизму:
 * 1) При отсутствии BottomNavigationView все работает как в обычной программе;
 * 2) Если есть BottomNavigationView, то у каждой вкладки свой стек:
 * 2.1) При нажатии кнопки назад сначала откатываемся по стеку у текущей вкладки, как стек текущей вкладки
 * закончился, переходим к стеку предыдущей вкладки (механизм двойного стэка)
 * 2.2) Также в таком режиме фрагмент добавляется сразу при запуске основной части, в позже скрывается и появляется
 * с помощью метода hide() и show() (необходимо учитывать если вы создаете свои фрагменты)
 *
 * @param fragmentManager Менеджер фрагментов
 * @param containerId ID контейнера
 * @param navigationBar BottomNavigationView
 */
class AppNavigationManager(
        val fragmentManager: FragmentManager,
        val containerId: Int,
        val navigationBar: BottomNavigationView
) : NavigationManager, BottomNavigationView.OnNavigationItemSelectedListener {

    private var blockCallback: Boolean = false
    private var backstack = LinkedList<Pair<Int, Fragment>>()
    private var loadedFragments = HashMap<Int, Fragment>()
    private var currentFragment: Fragment? = null
    private var currentItemTag = 0

    override fun showAuthPart() {
        currentFragment = AuthPhoneFragment()
        loadedFragments.clear()
        backstack.clear()

        val fragmentTransaction = fragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.animator_fragment_enter_right,
                        R.animator.animator_fragment_exit_right
                )

        fragmentManager.fragments.forEach {
            fragmentTransaction.remove(it)
        }

        fragmentTransaction
                .add(containerId, currentFragment!!, UUID.randomUUID().toString())
                .commit()

        navigationBar.visibility = View.GONE
    }

    override fun showMainPart() {
        currentFragment = null
        backstack.clear()

        val transaction = fragmentManager.beginTransaction()

        loadedFragments[PEOPLE_NAVBAR_ITEM_ID] = PeopleFragment()
        loadedFragments[DIALOGS_NAVBAR_ITEM_ID] = DialogsFragment()
        loadedFragments[PROFILE_NAVBAR_ITEM_ID] = ProfileFragment()

        fragmentManager.fragments.forEach {
            transaction.remove(it)
        }

        loadedFragments.forEach { pair ->
            transaction.add(containerId, pair.value, UUID.randomUUID().toString())
            transaction.hide(pair.value)
        }

        transaction.commit()

        navigationBar.selectedItemId = PEOPLE_NAVBAR_ITEM_ID
        navigationBar.visibility = View.VISIBLE
    }

    override fun showChildFragment(fragment: Fragment) {
        val fragmentTransaction = fragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.animator_fragment_enter_right,
                        R.animator.animator_fragment_exit_right
                )

        fragmentManager.fragments.forEach {
            fragmentTransaction.hide(it)
        }

        if (!fragment.isAdded) {
            fragmentTransaction.add(containerId, fragment, UUID.randomUUID().toString())
        }

        if (currentFragment != null && !isBackstackContainsCurrentFragment()) {
            backstack.add(Pair(currentItemTag, currentFragment!!))
        }

        currentFragment = fragment

        fragmentTransaction
                .show(fragment)
                .commit()
    }

    override fun popBackstack(): Boolean {
        if (backstack.isEmpty() || currentFragment == null) {
            return false
        }

        val fragmentTransaction = fragmentManager.beginTransaction()
        val backstackIterator = backstack.descendingIterator()
        var backstackPair: Pair<Int, Fragment>? = null

        while (backstackIterator.hasNext()) {
            val pair = backstackIterator.next()

            if (pair.second == currentFragment) {
                backstackIterator.remove()
                continue
            }

            if (pair.first == currentItemTag) {
                backstackIterator.remove()
                backstackPair = pair
                break
            }
        }

        if (backstackPair == null) {
            backstackPair = backstack.removeLast()!!
        }

        val prevFragment = backstackPair.second
        val prevItemTag = backstackPair.first

        if (prevItemTag >= currentItemTag) {
            fragmentTransaction.setCustomAnimations(
                    R.animator.animator_fragment_enter_right,
                    R.animator.animator_fragment_exit_right
            )
        } else if (prevItemTag < currentItemTag) {
            fragmentTransaction.setCustomAnimations(
                    R.animator.animator_fragment_enter_left,
                    R.animator.animator_fragment_exit_left
            )
        }

        if (isFragmentLoaded(currentFragment!!)) {
            fragmentTransaction.hide(currentFragment!!)
        } else {
            fragmentTransaction.remove(currentFragment!!)
        }

        fragmentTransaction
                .show(prevFragment)
                .commit()

        blockCallback = true
        navigationBar.selectedItemId = prevItemTag
        blockCallback = false

        currentFragment = prevFragment
        currentItemTag = prevItemTag

        return true
    }

    override fun restoreState(bundle: Bundle): Boolean {
        val currentFragmentTag = bundle.getString(CURRENT_FRAGMENT_TAG_EXTRA_KEY)

        currentFragment = fragmentManager.findFragmentByTag(currentFragmentTag)
        currentItemTag = bundle.getInt(CURRENT_ITEM_TAG_EXTRA_KEY)

        val backstackSize = bundle.getInt(BACKSTACK_SIZE_EXTRA_KEY)

        for (i in 0 until backstackSize) {
            val itemTag = bundle.getInt("$BACKSTACK_ITEM_TAG_AT_INDEX_EXTRA_KEY$i")
            val fragmentTag = bundle.getString("$BACKSTACK_FRAGMENT_TAG_AT_INDEX_EXTRA_KEY$i")
            val fragment = fragmentManager.findFragmentByTag(fragmentTag)!!

            backstack.add(Pair(itemTag, fragment))
        }

        val loadedFragmentsCount = bundle.getInt(LOADED_FRAGMENTS_COUNT_EXTRA_KEY)

        for (i in 0 until loadedFragmentsCount) {
            val itemTag = bundle.getInt("$LOADED_FRAGMENT_ITEM_TAG_AT_INDEX_EXTRA_KEY$i")
            val fragmentTag = bundle.getString("$LOADED_FRAGMENT_TAG_AT_INDEX_EXTRA_KEY$i")
            val fragment = fragmentManager.findFragmentByTag(fragmentTag)!!

            loadedFragments[itemTag] = fragment
        }

        navigationBar.visibility = if (bundle.getBoolean(NAVIGATION_BAR_VISIBLE_EXTRA_KEY)) {
            View.VISIBLE
        } else {
            View.GONE
        }

        configureNavigationBar()

        return false
    }

    override fun saveState(bundle: Bundle) {
        bundle.putBoolean(NAVIGATION_BAR_VISIBLE_EXTRA_KEY, navigationBar.visibility == View.VISIBLE)
        bundle.putString(CURRENT_FRAGMENT_TAG_EXTRA_KEY, currentFragment!!.tag)
        bundle.putInt(CURRENT_ITEM_TAG_EXTRA_KEY, currentItemTag)
        bundle.putInt(LOADED_FRAGMENTS_COUNT_EXTRA_KEY, loadedFragments.size)
        bundle.putInt(BACKSTACK_SIZE_EXTRA_KEY, backstack.size)

        var backstackIndex = 0
        val backstackIterator = backstack.iterator()

        while (backstackIterator.hasNext()) {
            val backstackPair = backstackIterator.next()

            bundle.putInt("$BACKSTACK_ITEM_TAG_AT_INDEX_EXTRA_KEY$backstackIndex", backstackPair.first)
            bundle.putString("$BACKSTACK_FRAGMENT_TAG_AT_INDEX_EXTRA_KEY$backstackIndex", backstackPair.second.tag)

            backstackIndex++
        }

        var loadedFragmentIndex = 0

        loadedFragments.forEach {
            bundle.putInt("$LOADED_FRAGMENT_ITEM_TAG_AT_INDEX_EXTRA_KEY$loadedFragmentIndex", it.key)
            bundle.putString("$LOADED_FRAGMENT_TAG_AT_INDEX_EXTRA_KEY$loadedFragmentIndex", it.value.tag)

            loadedFragmentIndex++
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (blockCallback) {
            return true
        }

        val tag = item.itemId

        if (currentFragment == loadedFragments[tag]) {
            return false
        }

        var backFromChildToRootFragment = false
        var itemLastFragment = getItemLastFragment(tag)
        val fragmentTransaction = fragmentManager.beginTransaction()

        if (tag >= currentItemTag) {
            fragmentTransaction.setCustomAnimations(
                    R.animator.animator_fragment_enter_right,
                    R.animator.animator_fragment_exit_right
            )
        } else if (tag < currentItemTag) {
            fragmentTransaction.setCustomAnimations(
                    R.animator.animator_fragment_enter_left,
                    R.animator.animator_fragment_exit_left
            )
        }

        fragmentManager.fragments.forEach {
            fragmentTransaction.hide(it)
        }

        if (currentFragment != null && currentItemTag == tag && !isFragmentLoaded(currentFragment!!)) {
            val loadedFragment = loadedFragments[tag]
            val backstackIterator = backstack.iterator()

            fragmentTransaction.remove(currentFragment!!)

            while (backstackIterator.hasNext()) {
                val pair = backstackIterator.next()

                if (pair.first != currentItemTag) {
                    continue
                }

                if (pair.second != loadedFragment) {
                    fragmentTransaction.remove(pair.second)
                }

                backstackIterator.remove()
            }

            itemLastFragment = loadedFragment!!
            backFromChildToRootFragment = true
        }

        if (!backFromChildToRootFragment && currentFragment != null) {
            val iterator = backstack.iterator()

            while (iterator.hasNext()) {
                val pair = iterator.next()

                if (pair.first == currentItemTag && pair.second == currentFragment!!) {
                    iterator.remove()
                    break
                }
            }

            backstack.add(Pair(currentItemTag, currentFragment!!))
        }

        fragmentTransaction
                .show(itemLastFragment)
                .commit()

        currentFragment = itemLastFragment
        currentItemTag = tag

        return true
    }

    override fun configureNavigationBar() {
        navigationBar.menu.apply {
            addItem(this, PEOPLE_NAVBAR_ITEM_ID, R.string.people, R.drawable.ic_group)
            addItem(this, DIALOGS_NAVBAR_ITEM_ID, R.string.messages, R.drawable.ic_chat_bubble)
            addItem(this, PROFILE_NAVBAR_ITEM_ID, R.string.profile, R.drawable.ic_account)
        }

        navigationBar.setOnNavigationItemSelectedListener(this)
    }

    private fun addItem(menu: Menu, @IdRes id: Int, @StringRes titleId: Int, @DrawableRes iconId: Int) {
        menu.add(0, id, 0, titleId).apply {
            setIcon(iconId)
        }
    }

    private fun isFragmentLoaded(fragment: Fragment): Boolean {
        return loadedFragments.entries.find {
            it.value == fragment
        } != null
    }

    private fun isBackstackContainsCurrentFragment(): Boolean {
        return backstack.find { pair ->
            pair.first == currentItemTag && pair.second == currentFragment!!
        } != null
    }

    private fun getItemLastFragment(tag: Int): Fragment {
        return backstack.findLast { pair ->
            pair.first == tag
        }?.second ?: loadedFragments[tag]!!
    }
}