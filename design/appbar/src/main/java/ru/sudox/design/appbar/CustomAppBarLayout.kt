package ru.sudox.design.appbar

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getIntegerOrThrow
import androidx.core.content.res.use
import androidx.core.graphics.ColorUtils
import androidx.core.view.updateLayoutParams
import ru.sudox.design.appbar.vos.AppBarLayoutVO

class CustomAppBarLayout : com.google.android.material.appbar.AppBarLayout {

    private var strokeWidth = 0
    private var strokeColor = 0
    private var strokeShown = true
    private var strokeAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
        addUpdateListener {
            val alpha = (255 * it.animatedValue as Float).toInt()
            val current = ColorUtils.setAlphaComponent(strokeColor, alpha)

            getStrokeDrawable().setStroke(strokeWidth, current)
        }
    }

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
            strokeWidth = it.getDimensionPixelSizeOrThrow(R.styleable.CustomAppBarLayout_strokeWidth)
            strokeColor = it.getColorOrThrow(R.styleable.CustomAppBarLayout_strokeColor)
            strokeAnimator.duration = it.getIntegerOrThrow(R.styleable.CustomAppBarLayout_strokeAnimationDuration).toLong()
        }
    }

    fun toggleStrokeShowing(toggle: Boolean, withAnimation: Boolean) {
        if (strokeShown == toggle) {
            return
        }

        strokeShown = toggle

        if (strokeAnimator!!.isRunning) {
            strokeAnimator!!.cancel()
        }

        if (withAnimation) {
            if (childCount > 1 || toggle) {
                strokeAnimator!!.start()
            } else {
                strokeAnimator!!.reverse()
            }
        } else if (childCount > 1) {
            getStrokeDrawable().setStroke(strokeWidth, strokeColor)
        } else if (toggle) {
            getStrokeDrawable().setStroke(strokeWidth, strokeColor)
        } else {
            getStrokeDrawable().setStroke(strokeWidth, Color.TRANSPARENT)
        }
    }

    private fun getStrokeDrawable() = (background as LayerDrawable).getDrawable(0) as GradientDrawable
}