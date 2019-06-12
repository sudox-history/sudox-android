package com.sudox.design.helpers

import android.content.res.Resources
import android.util.TypedValue
import com.sudox.common.annotations.Checked
import com.sudox.design.R

@Checked
fun Resources.Theme.getControlHighlightColor(): Int = with(TypedValue()) {
    resolveAttribute(R.attr.colorControlHighlight, this, true)
    data
}