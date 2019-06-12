package com.sudox.design.helpers

import androidx.core.text.TextDirectionHeuristicCompat
import androidx.core.text.TextDirectionHeuristicsCompat
import androidx.core.view.ViewCompat
import android.view.View
import com.sudox.common.annotations.Checked

@Checked
fun View.isTextRtl(text: String): Boolean {
    return getTextDirectionHeuristics().isRtl(text, 0, text.length)
}

@Checked
fun View.isLayoutRtl(): Boolean {
    return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL
}

@Checked
fun View.getTextDirectionHeuristics(): TextDirectionHeuristicCompat {
    return if (isLayoutRtl()) {
        TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL
    } else {
        TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR
    }
}