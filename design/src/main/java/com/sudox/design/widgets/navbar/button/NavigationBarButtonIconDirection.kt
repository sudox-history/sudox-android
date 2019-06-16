package com.sudox.design.widgets.navbar.button

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(NavigationBarButtonIconDirection.START, NavigationBarButtonIconDirection.END)
annotation class NavigationBarButtonIconDirection {
    companion object {
        const val START = 0
        const val END = 1
        const val DEFAULT = START
    }
}