package com.sudox.design.mityushkinlayout.yankintemplates

import android.graphics.Rect
import android.view.View
import com.sudox.design.mityushkinlayout.MityushkinLayout
import com.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import com.sudox.design.mityushkinlayout.MityushkinLayoutTemplate
import com.sudox.design.roundedview.RoundedView

object SevenItemsYankinTemplate : MityushkinLayoutTemplate {

    override var dependsFromChildSize = false

    override fun layout(widthMeasureSpec: Int, heightMeasureSpec: Int, adapter: MityushkinLayoutAdapter, layout: MityushkinLayout): Array<Rect> {
        (adapter as YankinTemplatesAdapter).let {
            val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
            val firstLineChildWidth = (widthSize - 2 * it.marginBetweenViews) / 3
            val secondLineChildWidth = (widthSize - 3 * it.marginBetweenViews) / 4

            val secondImageLeft = firstLineChildWidth + it.marginBetweenViews
            val secondImageRight = secondImageLeft + firstLineChildWidth
            val thirdImageLeft = secondImageRight + it.marginBetweenViews
            val thirdImageRight = thirdImageLeft + firstLineChildWidth

            val secondLineTop = it.threeAndMoreViewsHeight + it.marginBetweenViews
            val secondLineBottom = it.threeAndMoreViewsHeight + secondLineTop
            val fourthImageLeft = secondLineChildWidth + it.marginBetweenViews
            val fourthImageRight = fourthImageLeft + secondLineChildWidth
            val fifthImageLeft = fourthImageRight + it.marginBetweenViews
            val fifthImageRight = fifthImageLeft + secondLineChildWidth
            val sixthImageLeft = fifthImageRight + it.marginBetweenViews
            val sixthImageRight = sixthImageLeft + secondLineChildWidth

            val firstChild = layout.getChildAt(0)
            val secondChild = layout.getChildAt(1)
            val thirdChild = layout.getChildAt(2)
            val fourthChild = layout.getChildAt(3)
            val fifthChild = layout.getChildAt(4)
            val sixthChild = layout.getChildAt(5)
            val seventhChild = layout.getChildAt(6)

            if (firstChild is RoundedView) {
                firstChild.topLeftCropRadius = it.childBorderRadius
                firstChild.topRightCropRadius = it.childBorderRadius
                firstChild.bottomLeftCropRadius = it.childBorderRadius
                firstChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (secondChild is RoundedView) {
                secondChild.topLeftCropRadius = it.childBorderRadius
                secondChild.topRightCropRadius = it.childBorderRadius
                secondChild.bottomLeftCropRadius = it.childBorderRadius
                secondChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (thirdChild is RoundedView) {
                thirdChild.topLeftCropRadius = it.childBorderRadius
                thirdChild.topRightCropRadius = it.cornerBorderRadius
                thirdChild.bottomLeftCropRadius = it.childBorderRadius
                thirdChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (fourthChild is RoundedView) {
                fourthChild.topLeftCropRadius = it.childBorderRadius
                fourthChild.topRightCropRadius = it.childBorderRadius
                fourthChild.bottomLeftCropRadius = it.cornerBorderRadius
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
                sixthChild.bottomLeftCropRadius = it.childBorderRadius
                sixthChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (seventhChild is RoundedView) {
                seventhChild.topLeftCropRadius = it.childBorderRadius
                seventhChild.topRightCropRadius = it.childBorderRadius
                seventhChild.bottomLeftCropRadius = it.childBorderRadius
                seventhChild.bottomRightCropRadius = it.cornerBorderRadius
            }

            return arrayOf(
                    Rect(0, 0, firstLineChildWidth, it.threeAndMoreViewsHeight),
                    Rect(secondImageLeft, 0, secondImageRight, it.threeAndMoreViewsHeight),
                    Rect(thirdImageLeft, 0, thirdImageRight, it.threeAndMoreViewsHeight),
                    Rect(0, secondLineTop, secondLineChildWidth, secondLineBottom),
                    Rect(fourthImageLeft, secondLineTop, fourthImageRight, secondLineBottom),
                    Rect(fifthImageLeft, secondLineTop, fifthImageRight, secondLineBottom),
                    Rect(sixthImageLeft, secondLineTop, sixthImageRight, secondLineBottom)
            )
        }
    }
}