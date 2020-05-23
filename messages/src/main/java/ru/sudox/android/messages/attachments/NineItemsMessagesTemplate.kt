package ru.sudox.android.messages.attachments

import android.graphics.Rect
import android.view.View
import ru.sudox.design.mityushkinlayout.MityushkinLayout
import ru.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import ru.sudox.design.mityushkinlayout.MityushkinLayoutTemplate
import ru.sudox.design.common.views.RoundedView

object NineItemsMessagesTemplate : MityushkinLayoutTemplate {

    override var dependsFromChildSize = false

    override fun layout(widthMeasureSpec: Int, heightMeasureSpec: Int, adapter: MityushkinLayoutAdapter, layout: MityushkinLayout): Array<Rect> {
        (adapter as MessagesTemplatesAdapter).let {
            val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
            val firstLineChildWidth = (widthSize - it.marginBetweenViews) / 2
            val secondLineChildWidth = (widthSize - 2 * it.marginBetweenViews) / 3
            val thirdLineChildWidth = (widthSize - 3 * it.marginBetweenViews) / 4

            val secondLineTop = it.threeAndMoreViewsHeight + it.marginBetweenViews
            val secondLineBottom = it.threeAndMoreViewsHeight + secondLineTop
            val thirdLineTop = secondLineBottom + it.marginBetweenViews
            val thirdLineBottom = thirdLineTop + it.threeAndMoreViewsHeight

            val fourthImageLeft = secondLineChildWidth + it.marginBetweenViews
            val fourthImageRight = fourthImageLeft + secondLineChildWidth
            val fifthImageLeft = fourthImageRight + it.marginBetweenViews
            val fifthImageRight = fifthImageLeft + secondLineChildWidth

            val seventhImageLeft = thirdLineChildWidth + it.marginBetweenViews
            val seventhImageRight = seventhImageLeft + thirdLineChildWidth
            val eighthImageLeft = seventhImageRight + it.marginBetweenViews
            val eighthImageRight = eighthImageLeft + thirdLineChildWidth
            val ninthImageLeft = eighthImageRight + it.marginBetweenViews
            val ninthImageRight = ninthImageLeft + thirdLineChildWidth

            val firstChild = layout.getChildAt(0)
            val secondChild = layout.getChildAt(1)
            val thirdChild = layout.getChildAt(2)
            val fourthChild = layout.getChildAt(3)
            val fifthChild = layout.getChildAt(4)
            val sixthChild = layout.getChildAt(5)
            val seventhChild = layout.getChildAt(6)
            val eighthChild = layout.getChildAt(7)
            val ninthChild = layout.getChildAt(8)

            if (firstChild is RoundedView) {
                if (it.alignToRight) {
                    firstChild.topLeftCropRadius = it.cornerBorderRadius
                } else {
                    firstChild.topLeftCropRadius = it.childBorderRadius
                }

                firstChild.topRightCropRadius = it.childBorderRadius
                firstChild.bottomLeftCropRadius = it.childBorderRadius
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
                secondChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (thirdChild is RoundedView) {
                thirdChild.topLeftCropRadius = it.childBorderRadius
                thirdChild.topRightCropRadius = it.childBorderRadius
                thirdChild.bottomLeftCropRadius = it.childBorderRadius
                thirdChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (fourthChild is RoundedView) {
                fourthChild.topLeftCropRadius = it.childBorderRadius
                fourthChild.topRightCropRadius = it.childBorderRadius
                fourthChild.bottomLeftCropRadius = it.childBorderRadius
                fourthChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (fifthChild is RoundedView) {
                fifthChild.topLeftCropRadius = it.childBorderRadius
                fifthChild.topRightCropRadius = it.childBorderRadius
                fifthChild.bottomLeftCropRadius = it.childBorderRadius
                fifthChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (sixthChild is RoundedView) {
                sixthChild.topLeftCropRadius = it.childBorderRadius
                sixthChild.topRightCropRadius = it.childBorderRadius
                sixthChild.bottomLeftCropRadius = it.cornerBorderRadius
                sixthChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (seventhChild is RoundedView) {
                seventhChild.topLeftCropRadius = it.childBorderRadius
                seventhChild.topRightCropRadius = it.childBorderRadius
                seventhChild.bottomLeftCropRadius = it.childBorderRadius
                seventhChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (eighthChild is RoundedView) {
                eighthChild.topLeftCropRadius = it.childBorderRadius
                eighthChild.topRightCropRadius = it.childBorderRadius
                eighthChild.bottomLeftCropRadius = it.childBorderRadius
                eighthChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (ninthChild is RoundedView) {
                ninthChild.topLeftCropRadius = it.childBorderRadius
                ninthChild.topRightCropRadius = it.childBorderRadius
                ninthChild.bottomLeftCropRadius = it.childBorderRadius
                ninthChild.bottomRightCropRadius = it.cornerBorderRadius
            }

            return arrayOf(
                    Rect(0, 0, firstLineChildWidth, it.threeAndMoreViewsHeight),
                    Rect(widthSize - firstLineChildWidth, 0, widthSize, it.threeAndMoreViewsHeight),
                    Rect(0, secondLineTop, secondLineChildWidth, secondLineBottom),
                    Rect(fourthImageLeft, secondLineTop, fourthImageRight, secondLineBottom),
                    Rect(fifthImageLeft, secondLineTop, fifthImageRight, secondLineBottom),
                    Rect(0, thirdLineTop, thirdLineChildWidth, thirdLineBottom),
                    Rect(seventhImageLeft, thirdLineTop, seventhImageRight, thirdLineBottom),
                    Rect(eighthImageLeft, thirdLineTop, eighthImageRight, thirdLineBottom),
                    Rect(ninthImageLeft, thirdLineTop, ninthImageRight, thirdLineBottom)
            )
        }
    }
}