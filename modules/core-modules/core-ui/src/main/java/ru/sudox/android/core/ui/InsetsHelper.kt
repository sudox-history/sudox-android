package ru.sudox.android.core.ui

import android.view.View
import androidx.core.view.updatePadding

/**
 * Проставляет отступы, переданные системой
 *
 * @param top Выставить верхний отступ?
 * @param bottom Выставить нижний отступ?
 */
fun View.applyInserts(top: Boolean, bottom: Boolean) {
    val originalTopPadding = paddingTop
    val originalBottomPadding = paddingBottom

    setOnApplyWindowInsetsListener { _, insets ->
        if (top) {
            updatePadding(top = originalTopPadding + insets.systemWindowInsetTop)
        }

        if (bottom) {
            updatePadding(bottom = originalBottomPadding + insets.systemWindowInsetBottom)
        }

        insets
    }

    requestApplyInsets()
}