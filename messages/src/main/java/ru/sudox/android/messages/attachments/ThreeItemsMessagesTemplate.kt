package ru.sudox.android.messages.attachments

import android.graphics.Rect
import android.view.View
import ru.sudox.design.mityushkinlayout.MityushkinLayout
import ru.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import ru.sudox.design.mityushkinlayout.MityushkinLayoutTemplate
import ru.sudox.design.common.views.RoundedView

object ThreeItemsMessagesTemplate : MityushkinLayoutTemplate {

    override var dependsFromChildSize = false

    override fun layout(widthMeasureSpec: Int, heightMeasureSpec: Int, adapter: MityushkinLayoutAdapter, layout: MityushkinLayout): Array<Rect> {
        (adapter as MessagesTemplatesAdapter).let {
            val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
            val childWidth = (widthSize - it.marginBetweenViews) / 2
            val secondLineTop = it.threeAndMoreViewsHeight + it.marginBetweenViews
            val secondLineBottom = it.threeAndMoreViewsHeight + secondLineTop
            val firstChild = layout.getChildAt(0)
            val secondChild = layout.getChildAt(1)
            val thirdChild = layout.getChildAt(2)

            if (firstChild is RoundedView) {
                if (adapter.alignToRight) {
                    firstChild.topLeftCropRadius = it.cornerBorderRadius
                    firstChild.topRightCropRadius = it.childBorderRadius
                } else {
                    firstChild.topLeftCropRadius = it.childBorderRadius
                    firstChild.topRightCropRadius = it.cornerBorderRadius
                }

                firstChild.bottomLeftCropRadius = it.childBorderRadius
                firstChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (secondChild is RoundedView) {
                secondChild.topLeftCropRadius = it.childBorderRadius
                secondChild.topRightCropRadius = it.childBorderRadius
                secondChild.bottomLeftCropRadius = it.cornerBorderRadius
                secondChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (thirdChild is RoundedView) {
                thirdChild.topLeftCropRadius = it.childBorderRadius
                thirdChild.topRightCropRadius = it.childBorderRadius
                thirdChild.bottomLeftCropRadius = it.childBorderRadius
                thirdChild.bottomRightCropRadius = it.cornerBorderRadius
            }

            return arrayOf(
                    Rect(0, 0, widthSize, it.threeAndMoreViewsHeight),
                    Rect(0, secondLineTop, childWidth, secondLineBottom),
                    Rect(widthSize - childWidth, secondLineTop, widthSize, secondLineBottom)
            )
        }
    }
}