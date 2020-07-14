package ru.sudox.android.core.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.appbar.MaterialToolbar
import java.util.*

/**
 * Связывает тулбар с менеджером фрагментов.
 * Включает отображение кнопки назад если есть возможность.
 *
 * @param activity Активность приложения.
 * @param fragmentManager Менеджер фрагментом.
 */
fun MaterialToolbar.setupWithFragmentManager(activity: FragmentActivity, fragmentManager: FragmentManager) {
    if (fragmentManager.backStackEntryCount > 0) {
        setNavigationIcon(R.drawable.ic_baseline_arrow_back)
        setNavigationOnClickListener { popBackStack((activity as AppCompatActivity).supportFragmentManager) }
    }
}

/**
 * Устанавливает анимации в пределах части приложения
 */
fun FragmentTransaction.setChildEnterAnimations(): FragmentTransaction {
    return setCustomAnimations(
        R.animator.animator_child_enter,
        R.animator.animator_child_exit,
        R.animator.animator_child_enter,
        R.animator.animator_child_exit
    )
}

/**
 * Устанавливает анимации переключения между частями приложени
 */
fun FragmentTransaction.setRootEnterAnimations(): FragmentTransaction {
    return setCustomAnimations(
        R.animator.animator_part_enter_right,
        R.animator.animator_part_exit_right,
        R.animator.animator_part_enter_left,
        R.animator.animator_part_exit_left
    )
}

/**
 * Убирает последний фрагмент из Backstack.
 * Сначала работает с вложенными фрагментами, а потом с основными.
 *
 * @param fragmentManager Стартовый менеджер фрагментов
 * @return True если фрагмент был удален, False если нет фрагментов.
 */
fun popBackStack(fragmentManager: FragmentManager): Boolean {
    var currentManager: FragmentManager? = fragmentManager
    val managers = Stack<FragmentManager>().apply { push(fragmentManager) }

    while (currentManager != null) {
        for (fragment in currentManager!!.fragments) {
            currentManager = null

            if (fragment.isVisible && fragment.childFragmentManager.fragments.size > 0) {
                currentManager = fragment.childFragmentManager
                managers.push(currentManager)
                break
            }
        }
    }

    var manager: FragmentManager? = null

    while (managers.isNotEmpty()) {
        val current = managers.pop()

        if (current.backStackEntryCount > 1) {
            current.popBackStack()
            return true
        } else if (current.backStackEntryCount == 1) {
            manager = current
        }
    }

    if (manager != null) {
        manager.popBackStack()
        return true
    }

    return false
}