package com.sudox.design.tabLayout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.use
import androidx.viewpager.widget.ViewPager
import com.sudox.design.R
import kotlin.math.min

class TabLayout : ViewGroup {

    private var buttons: Array<TabLayoutButton>? = null
    private var viewPager: ViewPager? = null
    private var indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var indicatorPaddingRight = 0
    private var indicatorPaddingLeft = 0
    private var indicatorHeight = 0
    private var indicatorMargin = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.tabLayoutStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.TabLayout, defStyleAttr, 0).use {
            indicatorPaint.color = it.getColorOrThrow(R.styleable.TabLayout_indicatorColor)
            indicatorPaddingRight = it.getDimensionPixelSize(R.styleable.TabLayout_indicatorPaddingRight, 0)
            indicatorPaddingLeft = it.getDimensionPixelSize(R.styleable.TabLayout_indicatorPaddingLeft, 0)
            indicatorHeight = it.getDimensionPixelSizeOrThrow(R.styleable.TabLayout_indicatorHeight)
            indicatorMargin = it.getDimensionPixelSizeOrThrow(R.styleable.TabLayout_indicatorMargin)
        }

        layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        val buttonsWidth = buttons?.sumBy {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
            it.measuredWidth
        } ?: 0

        val needWidth = paddingLeft + buttonsWidth + paddingRight
        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            availableWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            min(needWidth, availableWidth)
        } else {
            needWidth
        }

        val needHeight = paddingTop + (buttons?.elementAtOrNull(0)?.measuredHeight ?: 0) + paddingBottom
        val measuredHeight = if (heightMode == MeasureSpec.EXACTLY) {
            availableHeight
        } else if (heightMode == MeasureSpec.AT_MOST) {
            min(needHeight, availableHeight)
        } else {
            needHeight
        }

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var rightBorder: Int
        var leftBorder = paddingLeft
        val topBorder = paddingTop
        val bottomBorder = measuredHeight

        buttons?.forEach {
            rightBorder = leftBorder + it.measuredWidth
            it.layout(leftBorder, topBorder, rightBorder, bottomBorder)
            leftBorder = rightBorder
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
    }

    fun setViewPager(viewPager: ViewPager) {
        if (this.viewPager != viewPager) {
            removeAllViewsInLayout()

            this.viewPager = viewPager
            this.buttons = Array(viewPager.adapter!!.count) {
                val button = TabLayoutButton(context!!)

                addView(button)

                button.setText(viewPager.adapter!!.getPageTitle(it).toString())
                button.layoutParams = LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT
                )

                button
            }
        }
    }
}