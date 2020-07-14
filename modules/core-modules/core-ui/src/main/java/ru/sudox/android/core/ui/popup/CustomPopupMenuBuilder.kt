package ru.sudox.android.core.ui.popup

import android.annotation.SuppressLint
import android.content.Context
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.forEach

/**
 * MenuBuilder, сохраняющий выбранный элемент.
 * Предотвращает проход циклом при работе метода onBindViewHolder()
 *
 * @param context Контекст приложения/активности
 */
@SuppressLint("RestrictedApi")
class CustomPopupMenuBuilder(
    context: Context
) : MenuBuilder(context) {

    var selectedItem: MenuItem? = null
        private set

    var selectedItemId = 0
        set(@IdRes value) {
            forEach {
                it.isChecked = value == it.itemId
                it.isCheckable = value == it.itemId

                if (value == it.itemId) {
                    selectedItem = it
                }
            }

            field = value
        }
}