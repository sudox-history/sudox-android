package com.sudox.design.mityushkinlayout.yankintemplates

import android.graphics.Rect
import android.view.View
import com.sudox.design.mityushkinlayout.MityushkinLayout
import com.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import com.sudox.design.mityushkinlayout.MityushkinLayoutTemplate
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object SingleItemYankinTemplate : MityushkinLayoutTemplate {

    override fun layout(widthMeasureSpec: Int, heightMeasureSpec: Int, adapter: MityushkinLayoutAdapter, layout: MityushkinLayout): Array<Rect> {
        (adapter as YankinTemplatesAdapter).let {
            val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
            val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)

            val child = layout.getChildAt(0)
            val width = min(if (widthMode != View.MeasureSpec.EXACTLY) {
                child.measuredWidth
            } else {
                widthSize
            }, it.maxSingleViewWidth)

            val aspect = child.measuredHeight.toFloat() / child.measuredWidth.toFloat()
            val height = min(max((width * aspect).roundToInt(), it.minSingleViewHeight), it.maxSingleViewHeight)

            return arrayOf(Rect(0, 0, width, height))
        }
    }
}