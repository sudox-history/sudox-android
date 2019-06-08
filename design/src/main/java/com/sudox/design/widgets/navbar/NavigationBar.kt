package com.sudox.design.widgets.navbar

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.sudox.design.R
import com.sudox.design.widgets.navbar.button.NavigationBarButton
import com.sudox.design.widgets.navbar.button.NavigationBarButtonParams
import java.util.ArrayList

private const val BUTTONS_IN_END_COUNT = 3

class NavigationBar : ViewGroup {

    internal var buttonParams = NavigationBarButtonParams()
    internal var titleParams = NavigationBarTitleParams()

    var buttonStart = NavigationBarButton(context, buttonParams)
    var buttonsEnd = ArrayList<NavigationBarButton>(BUTTONS_IN_END_COUNT)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.navigationBarStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        with(context.obtainStyledAttributes(attrs, R.styleable.NavigationBar, defStyleAttr, R.style.NavigationBar)) {
            buttonParams.readFromAttrs(this, context.theme)
            titleParams.readFromAttrs(this, context.theme)
            recycle()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

    }
}