package com.sudox.design.tabLayout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.sudox.design.R
import kotlin.math.min

class TabLayout : ViewGroup, ViewPager.OnPageChangeListener, View.OnClickListener {

    private var buttons: Array<TabLayoutButton>? = null
    private var viewPager: ViewPager? = null
    private var fragmentsButtons = ArrayList<Pair<Fragment, TabLayoutButton>>()
    private var positionOffset = 0F
    private var position = 0

    private var indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var indicatorPaddingRight = 0
    private var indicatorPaddingLeft = 0
    private var indicatorHeight = 0
    private var indicatorMargin = 0
    private var indicatorTop = 0F

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

    fun setViewPager(viewPager: ViewPager) {
        if (this.viewPager == viewPager) {
            return
        }

        removeAllViewsInLayout()

        this.viewPager = viewPager
        this.viewPager!!.addOnPageChangeListener(this)
        this.buttons = Array(viewPager.adapter!!.count) {
            TabLayoutButton(context!!).apply {
                addView(this)

                fragmentsButtons.add((viewPager.adapter as FragmentPagerAdapter).getItem(it) to this)

                setOnClickListener(this@TabLayout)
                setText(viewPager.adapter!!.getPageTitle(it).toString())

                layoutParams = LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT
                )
            }
        }

        onPageSelected(viewPager.currentItem)
    }

    override fun onClick(view: View) {
        buttons!!.forEachIndexed { index, it ->
            it.setActive(view == it)

            if (view == it) {
                viewPager!!.setCurrentItem(index, true)
            }
        }
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

        indicatorTop = buttons!!.maxBy {
            it.getTextBottom()
        }!!.getTextBottom() + indicatorMargin.toFloat()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        val currentTabButton = buttons!![position]
        val currentTabIndicatorWidth = currentTabButton.getTextWidth() + indicatorPaddingRight + indicatorPaddingLeft
        var currentTabIndicatorLeft =  with(currentTabButton) { left + (right - left) / 2 - currentTabIndicatorWidth / 2F }
        var currentTabIndicatorRight = currentTabIndicatorLeft + currentTabIndicatorWidth.toFloat()

        if (position < viewPager!!.adapter!!.count - 1) {
            val nextTabButton = buttons!![position + 1]
            val nextTabButtonIndicatorWidth = nextTabButton.getTextWidth() + indicatorPaddingRight + indicatorPaddingLeft
            val nextTabIndicatorLeft =  with(nextTabButton) { left + (right - left) / 2 - nextTabButtonIndicatorWidth / 2F }
            val nextTabIndicatorRight = nextTabIndicatorLeft + nextTabButtonIndicatorWidth

            currentTabIndicatorLeft += positionOffset * (nextTabIndicatorLeft - currentTabIndicatorLeft)
            currentTabIndicatorRight += positionOffset * (nextTabIndicatorRight - currentTabIndicatorRight)
        }

        val indicatorBottom = indicatorTop + indicatorHeight.toFloat()
        val indicatorCornerRadius = indicatorHeight / 2F

        canvas.drawRoundRect(currentTabIndicatorLeft,
                indicatorTop,
                currentTabIndicatorRight,
                indicatorBottom,
                indicatorCornerRadius,
                indicatorCornerRadius,
                indicatorPaint
        )
    }

    override fun onPageSelected(position: Int) {
        buttons!!.forEachIndexed { index, it ->
            it.setActive(position == index)

            if (index == position) {
                viewPager!!.setCurrentItem(index, true)
            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        this.positionOffset = positionOffset
        this.position = position

        invalidate()
    }

    override fun onPageScrollStateChanged(state: Int) {}
}