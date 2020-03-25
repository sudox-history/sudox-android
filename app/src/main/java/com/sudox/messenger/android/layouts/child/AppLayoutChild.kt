package com.sudox.messenger.android.layouts.child

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.sudox.design.appbar.AppBar
import com.sudox.design.appbar.AppBarLayout
import com.sudox.design.viewlist.ViewListState

class AppLayoutChild : ViewGroup {

    val appBarLayout = AppBarLayout(context).apply {
        id = View.generateViewId()
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
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
        measureChild(appBarLayout, widthMeasureSpec, heightMeasureSpec)
        measureChild(frameLayout, widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), appBarLayout.measuredHeight + frameLayout.measuredHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        appBarLayout.layout(0, 0, appBarLayout.measuredWidth, appBarLayout.measuredHeight)

        val bottomBorder = appBarLayout.measuredHeight + frameLayout.measuredHeight

        frameLayout.layout(0, appBarLayout.measuredWidth, frameLayout.measuredWidth, bottomBorder)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return AppLayoutChildState(super.onSaveInstanceState()!!).apply {
            readFromView(this@AppLayoutChild)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        (state as AppLayoutChildState).apply {
            super.onRestoreInstanceState(superState)
            writeToView(this@AppLayoutChild)
        }
    }
}