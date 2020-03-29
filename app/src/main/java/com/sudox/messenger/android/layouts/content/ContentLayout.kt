package com.sudox.messenger.android.layouts.content

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.sudox.design.appbar.AppBar
import com.sudox.design.appbar.AppBarLayout

class ContentLayout : CoordinatorLayout {

    @Suppress("unused")
    val appBarLayout = AppBarLayout(context).apply {
        id = View.generateViewId()
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        appBar = AppBar(context)

        this@ContentLayout.addView(this)
    }

    val frameLayout = FrameLayout(context).apply {
        id = View.generateViewId()
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
            behavior = com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior()
        }

        this@ContentLayout.addView(this)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}