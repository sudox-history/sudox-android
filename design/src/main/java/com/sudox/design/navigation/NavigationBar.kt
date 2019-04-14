package com.sudox.design.navigation

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class NavigationBar : View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {

    }
}