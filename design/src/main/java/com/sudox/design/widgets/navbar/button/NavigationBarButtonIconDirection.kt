package com.sudox.design.widgets.navbar.button

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(NavigationBarButtonIconDirection.LEFT, NavigationBarButtonIconDirection.RIGHT)
annotation class NavigationBarButtonIconDirection {
    companion object {
        const val LEFT = 0
        const val RIGHT = 1
        const val DEFAULT = LEFT
    }
}