package com.sudox.design.edittext

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.sudox.design.edittext.layout.EditTextLayout
import com.sudox.design.edittext.layout.EditTextLayoutChild

open class BasicEditText : AppCompatEditText, EditTextLayoutChild {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun changeStrokeColor(layout: EditTextLayout, width: Int, color: Int) {
        (background as GradientDrawable).setStroke(width, color)
        invalidate()
    }
}