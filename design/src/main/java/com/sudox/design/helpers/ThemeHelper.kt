package com.sudox.design.helpers

import android.content.res.Resources
import android.util.TypedValue
import com.sudox.design.R

fun Resources.Theme.getControlHighlightColor(): Int = with(TypedValue()) {
    resolveAttribute(R.attr.colorControlHighlight, this, true)
    data
}