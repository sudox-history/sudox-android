package com.sudox.messenger.android.layouts.child

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.sudox.design.appbar.AppBar
import com.sudox.design.appbar.AppBarLayout
import com.sudox.design.saveableview.SaveableViewGroup

class AppLayoutChild : SaveableViewGroup<AppLayoutChild, AppLayoutChildState> {

    val appBarLayout = AppBarLayout(context).apply {
        id = View.generateViewId()
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        translationZ = Float.MAX_VALUE
        appBar = AppBar(context)
    }

    val frameLayout = FrameLayout(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        id = View.generateViewId()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addView(appBarLayout)
        addView(frameLayout)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        appBarLayout.measure(widthMeasureSpec, heightMeasureSpec)
        frameLayout.measure(widthMeasureSpec, heightMeasureSpec)

        val needHeight = appBarLayout.measuredHeight + frameLayout.measuredHeight

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val topBorder = if (appBarLayout.visibility == View.VISIBLE) {
            appBarLayout.layout(0, 0, appBarLayout.measuredWidth, appBarLayout.measuredHeight)
            appBarLayout.measuredHeight
        } else {
            appBarLayout.layout(0, 0, 0, 0)
            0
        }

        frameLayout.layout(0, topBorder, frameLayout.measuredWidth, measuredHeight)
    }

    override fun createStateInstance(superState: Parcelable): AppLayoutChildState {
        return AppLayoutChildState(superState)
    }
}