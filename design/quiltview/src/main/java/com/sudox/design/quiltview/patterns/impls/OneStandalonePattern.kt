package com.sudox.design.quiltview.patterns.impls

import android.view.View
import com.sudox.design.quiltview.QuiltView
import com.sudox.design.quiltview.patterns.Pattern
import com.sudox.design.quiltview.patterns.PatternAdapter
import kotlin.math.max
import kotlin.math.min

class OneStandalonePattern : Pattern {

    override fun measure(widthSize: Int, widthMode: Int, heightSize: Int, heightMode: Int, adapter: PatternAdapter, view: QuiltView): Pair<Int, Int> {
        (adapter as StandalonePatternAdapter).let {
            val child = view.getChildAt(0)
            val width = if (widthMode == View.MeasureSpec.EXACTLY) {
                max(min(widthSize, adapter.maxViewWidth), adapter.minViewWidth)
            } else {
                max(min(child.measuredWidth, adapter.maxViewWidth), adapter.minViewWidth)
            }

            val height = width * child.measuredHeight / child.measuredWidth
            val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

            child.measure(widthSpec, heightSpec)

            return Pair(if (widthMode == View.MeasureSpec.EXACTLY) {
                widthSize
            } else {
                width
            }, height)
        }
    }

    override fun layout(left: Int, top: Int, adapter: PatternAdapter, view: QuiltView) {
        view.getChildAt(0).let {
            val leftBorder = left + (view.measuredWidth / 2 - it.measuredWidth / 2)
            val rightBorder = leftBorder + it.measuredWidth

            it.layout(leftBorder, top, rightBorder, top + it.measuredHeight)
        }
    }
}