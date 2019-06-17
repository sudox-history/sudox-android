package com.sudox.design.helpers

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.util.StateSet
import android.view.View
import com.sudox.design.drawables.ripple.RippleMaskDrawable
import com.sudox.design.drawables.ripple.RippleMaskType

internal fun Context.getRippleColorState(): ColorStateList {
    val controlHighlightColor = theme.getControlHighlightColor()
    val states = arrayOf(StateSet.WILD_CARD)
    val colors = intArrayOf(controlHighlightColor)
    return ColorStateList(states, colors)
}

fun View.addRipple(@RippleMaskType maskType: Int = RippleMaskType.DEFAULT) {
    val colorStateList = context.getRippleColorState()
    val maskDrawable = RippleMaskDrawable(maskType)
    background = RippleDrawable(colorStateList, null, maskDrawable)
}