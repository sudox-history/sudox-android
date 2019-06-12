package com.sudox.design.helpers

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.util.StateSet
import android.view.View
import com.sudox.common.annotations.Checked
import com.sudox.design.drawables.ripple.RippleMaskDrawable
import com.sudox.design.drawables.ripple.RippleMaskType

@Checked
fun View.addRipple(): RippleMaskDrawable {
    val controlHighlightColor = context.theme.getControlHighlightColor()
    val colorStateList = ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(controlHighlightColor))
    val maskDrawable = RippleMaskDrawable(RippleMaskType.DEFAULT)
    background = RippleDrawable(colorStateList, null, maskDrawable)

    return maskDrawable
}