package ru.sudox.android.messages.attachments

import android.graphics.Rect
import android.view.View
import ru.sudox.design.mityushkinlayout.MityushkinLayout
import ru.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import ru.sudox.design.mityushkinlayout.MityushkinLayoutTemplate
import ru.sudox.design.common.views.RoundedView
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object SingleItemMessagesTemplate : MityushkinLayoutTemplate {

    override var dependsFromChildSize = true

    override fun layout(widthMeasureSpec: Int, heightMeasureSpec: Int, adapter: MityushkinLayoutAdapter, layout: MityushkinLayout): Array<Rect> {
        (adapter as MessagesTemplatesAdapter).let {
            val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
            val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
            val child = layout.getChildAt(0)

            if (child is RoundedView) {
                if (it.alignToRight) {
                    child.topLeftCropRadius = it.cornerBorderRadius
                    child.topRightCropRadius = it.childBorderRadius
                } else {
                    child.topLeftCropRadius = it.childBorderRadius
                    child.topRightCropRadius = it.cornerBorderRadius
                }

                child.bottomLeftCropRadius = it.cornerBorderRadius
                child.bottomRightCropRadius = it.cornerBorderRadius
            }

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