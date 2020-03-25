package com.sudox.messenger.android.layouts

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import com.sudox.design.navigationBar.NavigationBar
import com.sudox.design.saveableview.SaveableViewGroup
import com.sudox.messenger.android.layouts.content.ContentLayout

class AppLayout : SaveableViewGroup<AppLayout, AppLayoutState> {

    val contentLayout = ContentLayout(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        id = View.generateViewId()
    }

    val navigationBar = NavigationBar(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addView(contentLayout)
        addView(navigationBar)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var contentHeight = MeasureSpec.getSize(heightMeasureSpec)

        measureChild(navigationBar, widthMeasureSpec, heightMeasureSpec)

        if (navigationBar.visibility == View.VISIBLE) {
            contentHeight -= navigationBar.measuredHeight
        }

        contentLayout.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY))

        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (navigationBar.visibility == View.VISIBLE) {
            navigationBar.layout(0, measuredHeight - navigationBar.measuredHeight, navigationBar.measuredWidth, measuredHeight)
        } else {
            navigationBar.layout(0, 0, 0, 0)
        }

        contentLayout.layout(0, 0, contentLayout.measuredWidth, contentLayout.measuredHeight)
    }

    override fun createStateInstance(superState: Parcelable): AppLayoutState {
        return AppLayoutState(superState)
    }
}