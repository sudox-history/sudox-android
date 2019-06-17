package com.sudox.design.helpers

import android.view.View
import androidx.core.text.TextDirectionHeuristicCompat
import androidx.core.text.TextDirectionHeuristicsCompat

fun View.isLayoutRtl(): Boolean {
    return layoutDirection == View.LAYOUT_DIRECTION_RTL
}

fun View.getTextDirectionHeuristics(): TextDirectionHeuristicCompat {
    return if (isLayoutRtl()) {
        TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL
    } else {
        TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR
    }
}

fun View.isTextRtl(text: String): Boolean {
    return getTextDirectionHeuristics().isRtl(text, 0, text.length)
}