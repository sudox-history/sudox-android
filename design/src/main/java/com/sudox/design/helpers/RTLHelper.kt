package com.sudox.design.helpers

import android.view.View
import androidx.core.text.TextDirectionHeuristicsCompat

fun View.isLayoutRtl(): Boolean {
    return resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL ||
            layoutDirection == View.LAYOUT_DIRECTION_RTL
}

fun View.isTextRtl(text: String): Boolean {
    val heuristics = if (isLayoutRtl()) {
        TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL
    } else {
        TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR
    }

    return heuristics.isRtl(text, 0, text.length)
}