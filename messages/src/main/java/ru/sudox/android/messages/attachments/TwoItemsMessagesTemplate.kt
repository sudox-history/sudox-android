package ru.sudox.android.messages.attachments

import android.graphics.Rect
import android.view.View
import ru.sudox.design.mityushkinlayout.MityushkinLayout
import ru.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import ru.sudox.design.mityushkinlayout.MityushkinLayoutTemplate
import ru.sudox.design.common.views.RoundedView

object TwoItemsMessagesTemplate : MityushkinLayoutTemplate {

    override var dependsFromChildSize = false

    override fun layout(widthMeasureSpec: Int, heightMeasureSpec: Int, adapter: MityushkinLayoutAdapter, layout: MityushkinLayout): Array<Rect> {
        (adapter as MessagesTemplatesAdapter).let {
            val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
            val childWidth = (widthSize - it.marginBetweenViews) / 2
            val firstChild = layout.getChildAt(0)
            val secondChild = layout.getChildAt(1)

            if (firstChild is RoundedView) {
                if (it.alignToRight) {
                    firstChild.topLeftCropRadius = it.cornerBorderRadius
                } else {
                    firstChild.topLeftCropRadius = it.childBorderRadius
                }

                firstChild.topRightCropRadius = it.childBorderRadius
                firstChild.bottomLeftCropRadius = it.cornerBorderRadius
                firstChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (secondChild is RoundedView) {
                if (it.alignToRight) {
                    secondChild.topRightCropRadius = it.childBorderRadius
                } else {
                    secondChild.topRightCropRadius = it.cornerBorderRadius
                }

                secondChild.topLeftCropRadius = it.childBorderRadius
                secondChild.bottomLeftCropRadius = it.childBorderRadius
                secondChild.bottomRightCropRadius = it.cornerBorderRadius
            }

            return arrayOf(
                    Rect(0, 0, childWidth, it.twoViewsHeight),
                    Rect(widthSize - childWidth, 0, widthSize, it.twoViewsHeight)
            )
        }
    }
}