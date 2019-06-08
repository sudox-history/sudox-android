package com.sudox.design.widgets.etlayout.label

import android.annotation.SuppressLint
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Typeface
import com.sudox.design.R
import com.sudox.design.helpers.loadTypeface

private val styleAttrs = intArrayOf(
        android.R.attr.textSize,
        android.R.attr.textStyle,
        android.R.attr.fontFamily)

private const val TEXT_SIZE_ATTR_INDEX = 0
private const val TEXT_STYLE_ATTR_INDEX = 1
private const val FONT_FAMILY_ATTR_INDEX = 2

class EditTextLayoutLabelParams {

    internal var errorTextColor: Int = 0
    internal var textTypeface: Typeface? = null
    internal var textSize: Int = 0

    @SuppressLint("ResourceType")
    fun readFromAttrs(typedArray: TypedArray, theme: Resources.Theme) {
        val labelStyleResourceId = typedArray.getResourceId(R.styleable.EditTextLayout_labelStyle, -1)
        with(theme.obtainStyledAttributes(labelStyleResourceId, styleAttrs)) {
            val textStyle = getInt(TEXT_STYLE_ATTR_INDEX, -1)
            val fontFamily = getString(FONT_FAMILY_ATTR_INDEX)

            textSize = getDimensionPixelSize(TEXT_SIZE_ATTR_INDEX, -1)
            textTypeface = loadTypeface(fontFamily!!, textStyle)
            errorTextColor = typedArray.getColor(R.styleable.EditTextLayout_errorTextColor, -1)
            recycle()
        }
    }
}