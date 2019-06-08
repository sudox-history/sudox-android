package com.sudox.design.widgets.navbar

import android.annotation.SuppressLint
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Typeface
import com.sudox.design.R
import com.sudox.design.helpers.loadTypeface

private val styleAttrs = intArrayOf(
        android.R.attr.textSize,
        android.R.attr.textStyle,
        android.R.attr.textColor,
        android.R.attr.fontFamily)

private const val TEXT_SIZE_ATTR_INDEX = 0
private const val TEXT_STYLE_ATTR_INDEX = 1
private const val TEXT_COLOR_ATTR_INDEX = 2
private const val FONT_FAMILY_ATTR_INDEX = 4

class NavigationBarTitleParams {

    internal var textTypeface: Typeface? = null
    internal var textColor: Int = 0
    internal var textSize: Float = 0F

    @SuppressLint("ResourceType")
    fun readFromAttrs(typedArray: TypedArray, theme: Resources.Theme) {
        val titleStyleResourceId = typedArray.getResourceId(R.styleable.NavigationBar_titleStyle, -1)
        with(theme.obtainStyledAttributes(titleStyleResourceId, styleAttrs)) {
            val textStyle = typedArray.getInt(TEXT_STYLE_ATTR_INDEX, -1)
            val fontFamily = typedArray.getString(FONT_FAMILY_ATTR_INDEX)

            textSize = typedArray.getDimension(TEXT_SIZE_ATTR_INDEX, -1F)
            textColor = typedArray.getColor(TEXT_COLOR_ATTR_INDEX, -1)
            textTypeface = loadTypeface(fontFamily!!, textStyle)
            recycle()
        }
    }
}