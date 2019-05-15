package com.sudox.design.widgets.edittextlayout

import android.content.res.TypedArray
import com.sudox.design.R

class EditTextLayoutLabelParams {

    var errorTextColor: Int = 0
    var textSize: Int = 0

    fun readFromAttrs(typedArray: TypedArray) {
        errorTextColor = typedArray.getColor(R.styleable.EditTextLayout_labelErrorTextColor, 0)
        textSize = typedArray.getDimensionPixelSize(R.styleable.EditTextLayout_labelTextSize, 0)
    }
}