package com.sudox.design.widgets

import android.content.Context
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import com.sudox.android.R

class SheetButton : AppCompatImageButton {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {

    }
}