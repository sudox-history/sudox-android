package com.sudox.design.helpers

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.util.StateSet
import android.view.View
import com.sudox.design.drawables.ripple.RippleMaskDrawable
import com.sudox.design.drawables.ripple.RippleMaskType

private fun Context.getRippleColorState(): ColorStateList {
    val color = theme.getControlHighlightColor()
    val states = arrayOf(StateSet.WILD_CARD)
    val colors = intArrayOf(color)
    return ColorStateList(states, colors)
}

fun View.addRipple(@RippleMaskType maskType: Int = RippleMaskType.DEFAULT) {
    val color = context.getRippleColorState()
    val mask = RippleMaskDrawable(maskType)
    background = RippleDrawable(color, null, mask)
}