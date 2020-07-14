package ru.sudox.android.core.ui.lists.shadows

import android.view.Menu
import android.view.MenuInflater
import androidx.annotation.MenuRes
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

internal var MENU_INFLATER_SHADOW_CALLBACK: ((Menu) -> (Unit))? = null

/**
 * Mock-класс для компоновщика меню
 */
@Implements(MenuInflater::class)
class MenuInflaterShadow {

    @Implementation
    fun inflate(@MenuRes menuRes: Int, menu: Menu) {
        MENU_INFLATER_SHADOW_CALLBACK?.invoke(menu)
    }
}