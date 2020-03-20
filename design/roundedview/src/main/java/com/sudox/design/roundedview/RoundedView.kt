package com.sudox.design.roundedview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

abstract class RoundedView : View {

    private var

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
    }
}