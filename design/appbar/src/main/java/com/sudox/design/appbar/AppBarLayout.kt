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
                addViewInLayout(view, -1, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
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

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
}