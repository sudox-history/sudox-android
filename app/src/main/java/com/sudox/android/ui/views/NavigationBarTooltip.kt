package com.sudox.android.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.sudox.android.R
import kotlinx.android.synthetic.main.include_navbar_tooltip.view.*

class NavigationBarTooltip : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.include_navbar_tooltip, this)
    }

    fun setText(text: String) {
        navbarTooltipText.text = text
    }

    fun show() {

    }

    fun hide() {

    }
}