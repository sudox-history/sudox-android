package com.sudox.design.appbar

import android.content.Context
import android.util.AttributeSet
import com.sudox.design.appbar.vos.AppBarLayoutVO

class AppBarLayout : com.google.android.material.appbar.AppBarLayout {

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

            val views = value?.getViews(context)
            val viewsCount = views?.size ?: 0

            addViewInLayout(appBar, -1, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                scrollFlags = if (viewsCount > 0) {
                    LayoutParams.SCROLL_FLAG_SCROLL or LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                } else {
                    LayoutParams.SCROLL_FLAG_NO_SCROLL
                }
            })

            value?.getViews(context)?.forEachIndexed { index, view ->
                addViewInLayout(view, -1, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    scrollFlags = if (index == views!!.lastIndex) {
                        LayoutParams.SCROLL_FLAG_SNAP
                    } else {
                        LayoutParams.SCROLL_FLAG_SCROLL
                    }
                })
            }

            field = value
            requestLayout()
            invalidate()
        }

    var pixelsToLastChild = 0
        private set

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

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