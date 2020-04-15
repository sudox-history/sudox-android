package ru.sudox.design.bottomnavigationview

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.google.android.material.bottomnavigation.BottomNavigationView

fun BottomNavigationView.addItem(@IdRes id: Int, @StringRes titleId: Int, @DrawableRes iconId: Int) {
    menu.add(0, id, 0, titleId).apply {
        setIcon(iconId)
    }
}