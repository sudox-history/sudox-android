package ru.sudox.design.appbar

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.updateLayoutParams
import ru.sudox.design.appbar.vos.AppBarLayoutVO

class AppBarLayout : com.google.android.material.appbar.AppBarLayout {

    var appBar: AppBar? = null
        set(value) {
            if (field != null) {
                removeView(field)
            }

            if (value != null) {
                addView(value.apply {
                    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                }, 0)
            }

            field = value
            requestLayout()
            invalidate()
        }

    var vo: AppBarLayoutVO? = null
        set(value) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)

                if (child != appBar) {
                    removeView(this)
                }
            }

            val views = value?.getViews(context)
            val viewsCount = views?.size ?: 0

            appBar!!.updateLayoutParams<LayoutParams> {
                scrollFlags = if (viewsCount > 0) {
                    LayoutParams.SCROLL_FLAG_SCROLL or LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                } else {
                    LayoutParams.SCROLL_FLAG_NO_SCROLL
                }
            }

            value?.getViews(context)?.forEachIndexed { index, view ->
                val params = view.layoutParams

                addViewInLayout(view, -1, LayoutParams(params.width, params.height).apply {
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