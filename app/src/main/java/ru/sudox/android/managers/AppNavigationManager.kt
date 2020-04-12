package ru.sudox.android.managers

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.sudox.android.R
import ru.sudox.android.auth.phone.AuthPhoneFragment
import ru.sudox.android.core.CoreFragment
import ru.sudox.android.core.managers.NavigationManager
import ru.sudox.android.messages.DialogsFragment
import ru.sudox.android.people.PeopleFragment
import ru.sudox.android.people.ProfileFragment
import java.util.Stack

internal const val AUTH_TAG = 0
internal const val PEOPLE_TAG = 1
internal const val DIALOGS_TAG = 2
internal const val PROFILE_TAG = 3

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

    private var mainFragments: HashMap<Int, Lazy<CoreFragment>>? = null

    private fun createMainFragmentsMap(): HashMap<Int, Lazy<CoreFragment>> {
        return hashMapOf(
                PEOPLE_TAG to lazy { PeopleFragment() },
                DIALOGS_TAG to lazy { DialogsFragment() },
                PROFILE_TAG to lazy { ProfileFragment() }
        )
    }

    private var blockNavbarCallback = false
    private var tagsBackstack = Stack<Int>()
    private var childBackstack = HashMap<Int, Stack<CoreFragment>>()
    private var currentPart: AppNavigationPart? = null
    private var currentTag: Int = 0

    override fun showAuthPart() {
        val transaction = fragmentManager.beginTransaction()
        val fragment = AuthPhoneFragment()

        if (currentPart != null) {
            transaction.setCustomAnimations(R.animator.animator_fragment_enter_left, R.animator.animator_fragment_exit_left)
        } else {
            transaction.setTransition(TRANSIT_FRAGMENT_FADE)
        }

        transaction
                .replace(containerId, fragment)
                .commit()

        navigationBar.visibility = View.GONE

        currentPart = AppNavigationPart.AUTH
        currentTag = AUTH_TAG

        tagsBackstack.clear()
        childBackstack.clear()
        mainFragments!!.clear()

        childBackstack
                .getOrPut(currentTag, { Stack() })
                .push(fragment)

        tagsBackstack.push(currentTag)
    }

    override fun showMainPart() {
        val fragment = PeopleFragment()

        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.animator.animator_fragment_enter_right, R.animator.animator_fragment_exit_right)
                .replace(containerId, fragment)
                .commit()

        currentPart = AppNavigationPart.MAIN
        currentTag = PEOPLE_TAG

        tagsBackstack.clear()
        childBackstack.clear()
        mainFragments = createMainFragmentsMap()

        childBackstack
                .getOrPut(currentTag, { Stack() })
                .push(fragment)

        tagsBackstack.push(currentTag)

        blockNavbarCallback = true
        navigationBar.selectedItemId = PEOPLE_TAG
        blockNavbarCallback = false

        navigationBar.visibility = View.VISIBLE
    }

    override fun showChildFragment(fragment: CoreFragment) {
        childBackstack[currentTag]!!.push(fragment)

        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.animator.animator_fragment_enter_right, R.animator.animator_fragment_exit_right)
                .replace(containerId, fragment)
                .commit()
    }

    override fun popBackstack(): Boolean {
        val currentBackstack = childBackstack[currentTag]!!
        currentBackstack.pop()

        val transaction = fragmentManager.beginTransaction()

        if (currentBackstack.isEmpty()) {
            // Root to child or root to root

            childBackstack.remove(currentTag)
            tagsBackstack.pop()

            if (tagsBackstack.isEmpty()) {
                return false
            }

            val prevTag = tagsBackstack.peek()
            val prevFragment = childBackstack[prevTag]!!.peek()

            if (prevTag > currentTag) {
                transaction.setCustomAnimations(R.animator.animator_fragment_enter_right, R.animator.animator_fragment_exit_right)
            } else {
                transaction.setCustomAnimations(R.animator.animator_fragment_enter_left, R.animator.animator_fragment_exit_left)
            }

            transaction
                    .replace(containerId, prevFragment)
                    .commit()

            currentTag = prevTag

            blockNavbarCallback = true
            navigationBar.selectedItemId = currentTag
            blockNavbarCallback = false
        } else {
            // Child to root, or child to child in once tag

            transaction
                    .setCustomAnimations(R.animator.animator_fragment_enter_left, R.animator.animator_fragment_exit_left)
                    .replace(containerId, currentBackstack.peek())
                    .commit()
        }

        return true
    }

    override fun restoreState(bundle: Bundle): Boolean {
        return false
    }

    override fun saveState(bundle: Bundle) {
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (blockNavbarCallback) {
            return true
        }

        val tag = item.itemId
        val currentStack = childBackstack[currentTag]!!
        val transaction = fragmentManager.beginTransaction()

        if (tag == currentTag) {
            // It's root fragment!
            if (currentStack.size == 1) {
                currentStack.peek().resetFragment()
            } else {
                // Child to root where tag not changing
                repeat(currentStack.size - 1) {
                    currentStack.pop()
                }

                transaction
                        .setCustomAnimations(R.animator.animator_fragment_enter_left, R.animator.animator_fragment_exit_left)
                        .replace(containerId, currentStack.peek())
                        .commit()
            }

            return false
        } else {
            tagsBackstack.remove(tag)
            tagsBackstack.push(tag)

            val stack = childBackstack.getOrPut(tag, { Stack() })

            if (stack.isEmpty()) {
                stack.push(mainFragments!![tag]!!.value)
            }

            if (tag > currentTag) {
                transaction.setCustomAnimations(
                        R.animator.animator_fragment_enter_right,
                        R.animator.animator_fragment_exit_right
                )
            } else {
                transaction.setCustomAnimations(
                        R.animator.animator_fragment_enter_left,
                        R.animator.animator_fragment_exit_left
                )
            }

            transaction
                    .replace(containerId, stack.peek())
                    .commit()

            currentTag = tag
        }

        return true
    }

    override fun configureNavigationBar() {
        navigationBar.menu.apply {
            addItem(this, PEOPLE_TAG, R.string.people, R.drawable.ic_group)
            addItem(this, DIALOGS_TAG, R.string.messages, R.drawable.ic_chat_bubble)
            addItem(this, PROFILE_TAG, R.string.profile, R.drawable.ic_account)
        }

        navigationBar.setOnNavigationItemSelectedListener(this)
    }

    private fun addItem(menu: Menu, @IdRes id: Int, @StringRes titleId: Int, @DrawableRes iconId: Int) {
        menu.add(0, id, 0, titleId).apply {
            setIcon(iconId)
        }
    }
}