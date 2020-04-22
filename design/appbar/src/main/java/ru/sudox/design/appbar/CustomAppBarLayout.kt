package ru.sudox.design.appbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getIntegerOrThrow
import androidx.core.content.res.use
import androidx.core.view.updateLayoutParams
import ru.sudox.design.appbar.vos.AppBarLayoutVO

class CustomAppBarLayout : com.google.android.material.appbar.AppBarLayout {

    private var strokeWidth = 0
    private var strokeAnimationDuration = 0
    private var strokeColor = 0

    var appBar: AppBar? = null
        set(value) {
            if (field != null) {
                removeView(field)
            }

            if (value != null) {
                addView(value.apply {
                    id = View.generateViewId()
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
                    removeView(child)
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

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.customAppBarLayoutStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.CustomAppBarLayout, defStyleAttr, 0).use {
            strokeAnimationDuration = it.getIntegerOrThrow(R.styleable.CustomAppBarLayout_strokeAnimationDuration)
            strokeWidth = it.getDimensionPixelSizeOrThrow(R.styleable.CustomAppBarLayout_strokeWidth)
            strokeColor = it.getColorOrThrow(R.styleable.CustomAppBarLayout_strokeColor)
        }
    }

    fun toggleStrokeShowing(toggle: Boolean, withAnimation: Boolean) {
        val gradientDrawable = (background as LayerDrawable).getDrawable(0) as GradientDrawable

        if (childCount > 1) {
            gradientDrawable.setStroke(strokeWidth, strokeColor)
        } else if (toggle) {
            gradientDrawable.setStroke(strokeWidth, strokeColor)
        } else {
            gradientDrawable.setStroke(0, strokeColor)
        }
    }
}