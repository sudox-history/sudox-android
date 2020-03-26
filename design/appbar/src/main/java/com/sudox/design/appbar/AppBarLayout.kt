package com.sudox.design.appbar

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.sudox.design.appbar.vos.AppBarLayoutVO

class AppBarLayout : ViewGroup {

    var appBar: AppBar? = null
        set(value) {
            if (field != null) {
                removeView(field)
            }

            if (value != null) {
                addView(value, 0)
            }

            field = value
            requestLayout()
            invalidate()
        }

    var vo: AppBarLayoutVO? = null
        set(value) {
            removeAllViewsInLayout()
            addView(appBar)

            value?.getViews(context)?.forEach {
                addView(it)
            }

            field = value
            requestLayout()
            invalidate()
        }

    var pixelsToLastChild = 0
        private set

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.appbarLayoutStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        var height = paddingTop + paddingBottom

        for (i in 0 until childCount) {
            height += with(getChildAt(i)) {
                measureChild(this, widthMeasureSpec, heightMeasureSpec)
                measuredHeight
            }
        }

        pixelsToLastChild = height

        if (childCount > 1) {
            pixelsToLastChild -= getChildAt(childCount - 1).measuredHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var topBorder = paddingTop
        var bottomBorder: Int

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val leftBorder = measuredWidth / 2 - child.measuredWidth / 2
            val rightBorder = leftBorder + child.measuredWidth

            bottomBorder = topBorder + child.measuredHeight
            child.layout(leftBorder, topBorder, rightBorder, bottomBorder)
            topBorder = bottomBorder
        }
    }
}