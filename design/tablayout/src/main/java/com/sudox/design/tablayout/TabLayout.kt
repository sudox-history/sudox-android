package com.sudox.design.tablayout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.viewpager.widget.ViewPager
import com.sudox.design.common.calculateViewSize
import kotlin.math.max

class TabLayout : ViewGroup, ViewPager.OnPageChangeListener, View.OnClickListener {

    private var indicatorTop = 0
    private var linkedViewPager: ViewPager? = null
    private var indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var positionOffset = 0F
    private var position = 0

    private var activeColor = 0
    private var inactiveColor = 0
    private var tabNameStyleId = 0
    private var marginBetweenTabs = 0
    private var indicatorPadding = 0
    private var indicatorWidth = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.tabLayoutStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.TabLayout, defStyleAttr, 0).use {
            activeColor = it.getColorOrThrow(R.styleable.TabLayout_activeColor)
            inactiveColor = it.getColorOrThrow(R.styleable.TabLayout_inactiveColor)
            tabNameStyleId = it.getResourceIdOrThrow(R.styleable.TabLayout_tabNameStyle)
            marginBetweenTabs = it.getDimensionPixelSizeOrThrow(R.styleable.TabLayout_marginBetweenTabs)
            indicatorPadding = it.getDimensionPixelSizeOrThrow(R.styleable.TabLayout_indicatorPadding)
            indicatorWidth = it.getDimensionPixelSizeOrThrow(R.styleable.TabLayout_indicatorWidth)
        }

        indicatorPaint.color = activeColor
    }

    /**
     * Синхронизирует вкладки ViewPager'а с данным TabLayout'ом
     * Также синхронизирует работу индикатора.
     *
     * @param viewPager ViewPager, с которым нужно синхронизироваться.
     */
    fun syncWithViewPager(viewPager: ViewPager) {
        removeAllViewsInLayout()

        viewPager.let {
            it.addOnPageChangeListener(this)

            for (i in 0 until it.adapter!!.count) {
                addViewInLayout(AppCompatTextView(ContextThemeWrapper(context, tabNameStyleId)).apply {
                    setOnClickListener(this@TabLayout)

                    text = it.adapter!!
                            .getPageTitle(i)
                            .toString()
                }, i, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT))
            }

            linkedViewPager = it
            onPageSelected(it.currentItem)
            requestLayout()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var needHeight = paddingTop + paddingBottom + indicatorWidth
        var needWidth = max(paddingLeft + paddingRight, indicatorPadding * 2) + (childCount - 1) * marginBetweenTabs

        for (i in 0 until childCount) {
            needWidth += with(getChildAt(i)) {
                measureChild(this, widthMeasureSpec, heightMeasureSpec)

                needHeight = if (i == 0) {
                    needWidth + measuredHeight
                } else {
                    max(measuredHeight, needHeight)
                }

                measuredWidth
            }
        }

        setMeasuredDimension(
                calculateViewSize(widthMeasureSpec, needWidth),
                calculateViewSize(heightMeasureSpec, needHeight)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var leftBorder = max(paddingLeft, indicatorPadding)
        var rightBorder: Int

        for (i in 0 until childCount) {
            (getChildAt(i) as AppCompatTextView).let {
                val topBorder = measuredHeight / 2 - it.measuredHeight / 2
                val bottomBorder = topBorder + it.measuredHeight

                rightBorder = leftBorder + it.measuredWidth
                it.layout(leftBorder, topBorder, rightBorder, bottomBorder)
                leftBorder = rightBorder + marginBetweenTabs

                indicatorTop = max(bottomBorder, indicatorTop)
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        val currentTabButton = getChildAt(position)
        val currentTabIndicatorWidth = with(currentTabButton) { measuredWidth - paddingLeft - paddingRight } + indicatorPadding * 2
        var currentTabIndicatorLeft = with(currentTabButton) { left + (right - left) / 2 - currentTabIndicatorWidth / 2F }
        var currentTabIndicatorRight = currentTabIndicatorLeft + currentTabIndicatorWidth.toFloat()

        if (position < linkedViewPager!!.adapter!!.count - 1) {
            val nextTabButton = getChildAt(position + 1)
            val nextTabButtonIndicatorWidth = with(nextTabButton) { measuredWidth - paddingLeft - paddingRight } + indicatorPadding * 2
            val nextTabIndicatorLeft = with(nextTabButton) { left + (right - left) / 2 - nextTabButtonIndicatorWidth / 2F }
            val nextTabIndicatorRight = nextTabIndicatorLeft + nextTabButtonIndicatorWidth

            currentTabIndicatorLeft += positionOffset * (nextTabIndicatorLeft - currentTabIndicatorLeft)
            currentTabIndicatorRight += positionOffset * (nextTabIndicatorRight - currentTabIndicatorRight)
        }

        val indicatorBottom = indicatorTop + indicatorWidth.toFloat()
        val indicatorCornerRadius = indicatorWidth / 2F

        canvas.drawRoundRect(currentTabIndicatorLeft,
                indicatorTop.toFloat(),
                currentTabIndicatorRight,
                indicatorBottom,
                indicatorCornerRadius,
                indicatorCornerRadius,
                indicatorPaint
        )

        super.dispatchDraw(canvas)
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        this.position = position
        this.positionOffset = positionOffset

        invalidate()
    }

    override fun onPageSelected(position: Int) {
        for (i in 0 until childCount) {
            (getChildAt(i) as AppCompatTextView).setTextColor(if (i == position) {
                activeColor
            } else {
                inactiveColor
            })
        }
    }

    override fun onClick(view: View) {
        linkedViewPager!!.setCurrentItem(indexOfChild(view), true)
    }
}