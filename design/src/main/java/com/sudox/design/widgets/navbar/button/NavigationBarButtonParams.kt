package com.sudox.design.widgets.navbar.button

import android.annotation.SuppressLint
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Typeface
import com.sudox.design.R
import com.sudox.design.helpers.loadTypeface

internal val buttonStyleAttrs = intArrayOf(
        android.R.attr.textSize,
        android.R.attr.textStyle,
        android.R.attr.textColor,
        android.R.attr.paddingLeft,
        android.R.attr.paddingRight,
        android.R.attr.fontFamily,
        R.attr.iconTextMargin,
        R.attr.iconTintColor)

private const val TEXT_SIZE_ATTR_INDEX = 0
private const val TEXT_STYLE_ATTR_INDEX = 1
private const val TEXT_COLOR_ATTR_INDEX = 2
private const val PADDING_LEFT_ATTR_INDEX = 3
private const val PADDING_RIGHT_ATTR_INDEX = 4
private const val FONT_FAMILY_ATTR_INDEX = 5
private const val ICON_TEXT_MARGIN_ATTR_INDEX = 6
private const val ICON_TINT_COLOR_ATTR_INDEX = 7

class NavigationBarButtonParams {

    internal var leftPadding: Int = 0
    internal var rightPadding: Int = 0
    internal var iconTintColor: Int = 0
    internal var iconTextMargin: Int = 0
    internal var textTypeface: Typeface? = null
    internal var textColor: Int = 0
    internal var textSize: Float = 0F

    @SuppressLint("ResourceType")
    fun readFromAttrs(typedArray: TypedArray, theme: Resources.Theme) {
        val buttonStyleResourceId = typedArray.getResourceId(R.styleable.NavigationBar_buttonsStyle, -1)

        with(theme.obtainStyledAttributes(buttonStyleResourceId, buttonStyleAttrs)) {
            val textStyle = getInt(TEXT_STYLE_ATTR_INDEX, -1)
            val fontFamily = getString(FONT_FAMILY_ATTR_INDEX)

            textSize = getDimension(TEXT_SIZE_ATTR_INDEX, -1F)
            textColor = getColor(TEXT_COLOR_ATTR_INDEX, -1)
            textTypeface = loadTypeface(fontFamily!!, textStyle)
            iconTintColor = getColor(ICON_TINT_COLOR_ATTR_INDEX, -1)
            iconTextMargin = getDimensionPixelSize(ICON_TEXT_MARGIN_ATTR_INDEX, -1)
            leftPadding = getDimensionPixelSize(PADDING_LEFT_ATTR_INDEX, -1)
            rightPadding = getDimensionPixelSize(PADDING_RIGHT_ATTR_INDEX, -1)
            recycle()
        }
    }
}