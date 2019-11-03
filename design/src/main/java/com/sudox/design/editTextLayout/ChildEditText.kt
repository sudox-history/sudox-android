package com.sudox.design.editTextLayout

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

open class ChildEditText : AppCompatEditText, EditTextLayoutChild {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun getInstance(): View {
        return this
    }

    override fun setStroke(width: Int, color: Int) {
        (background as GradientDrawable).setStroke(width, color)
    }
}