package com.sudox.design.widgets.etlayout

import android.annotation.SuppressLint
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Typeface
import com.sudox.design.R
import com.sudox.design.helpers.loadTypeface

class EditTextLayoutLabelParams {

    var errorTextColor: Int = 0
    var textTypeface: Typeface? = null
    var textSize: Int = 0

    companion object {
        // Warning! All attrs must be ordered by id.
        private val styleAttrs = intArrayOf(
                android.R.attr.textSize,
                android.R.attr.textStyle,
                android.R.attr.fontFamily)
    }

    @SuppressLint("ResourceType")
    fun readFromAttrs(typedArray: TypedArray, theme: Resources.Theme) {
        val labelStyleResourceId = typedArray.getResourceId(R.styleable.EditTextLayout_labelStyle, -1)
        val labelStyleTypedArray = theme.obtainStyledAttributes(labelStyleResourceId, styleAttrs)
        val textStyle = labelStyleTypedArray.getInt(1, -1)
        val fontFamily = labelStyleTypedArray.getString(2)

        textSize = labelStyleTypedArray.getDimensionPixelSize(0, -1)
        textTypeface = loadTypeface(fontFamily!!, textStyle)
        errorTextColor = typedArray.getColor(R.styleable.EditTextLayout_errorTextColor, -1)

        labelStyleTypedArray.recycle()
    }
}