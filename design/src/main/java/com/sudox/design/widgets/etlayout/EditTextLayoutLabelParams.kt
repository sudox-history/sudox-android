package com.sudox.design.widgets.etlayout

import android.annotation.SuppressLint
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Typeface
import com.sudox.design.R
import com.sudox.design.helpers.loadTypeface

class EditTextLayoutLabelParams {

    internal var errorTextColor: Int = 0
    internal var textTypeface: Typeface? = null
    internal var textSize: Int = 0

    companion object {
        // Warning! All attrs must be ordered by id.
        private val styleAttrs = intArrayOf(
                android.R.attr.textSize,
                android.R.attr.textStyle,
                android.R.attr.fontFamily)

        private const val TEXT_SIZE_ATTR_INDEX = 0
        private const val TEXT_STYLE_ATTR_INDEX = 1
        private const val FONT_FAMILY_ATTR_INDEX = 2
    }

    @SuppressLint("ResourceType")
    fun readFromAttrs(typedArray: TypedArray, theme: Resources.Theme) {
        val labelStyleResourceId = typedArray.getResourceId(R.styleable.EditTextLayout_labelStyle, -1)
        val labelStyleTypedArray = theme.obtainStyledAttributes(labelStyleResourceId, styleAttrs)
        val textStyle = labelStyleTypedArray.getInt(TEXT_STYLE_ATTR_INDEX, -1)
        val fontFamily = labelStyleTypedArray.getString(FONT_FAMILY_ATTR_INDEX)

        textSize = labelStyleTypedArray.getDimensionPixelSize(TEXT_SIZE_ATTR_INDEX, -1)
        textTypeface = loadTypeface(fontFamily!!, textStyle)
        errorTextColor = typedArray.getColor(R.styleable.EditTextLayout_errorTextColor, -1)

        labelStyleTypedArray.recycle()
    }
}