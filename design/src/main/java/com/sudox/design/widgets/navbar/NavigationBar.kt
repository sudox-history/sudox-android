package com.sudox.design.widgets.navbar

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

class NavigationBar : ViewGroup {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        TODO("not implemented")
    }
}