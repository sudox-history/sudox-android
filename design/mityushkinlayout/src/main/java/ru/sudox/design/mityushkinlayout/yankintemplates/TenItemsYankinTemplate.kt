package ru.sudox.design.mityushkinlayout.yankintemplates

import android.graphics.Rect
import android.view.View
import ru.sudox.design.mityushkinlayout.MityushkinLayout
import ru.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import ru.sudox.design.mityushkinlayout.MityushkinLayoutTemplate
import ru.sudox.design.common.views.RoundedView

object TenItemsYankinTemplate : MityushkinLayoutTemplate {

    override var dependsFromChildSize = false

    override fun layout(widthMeasureSpec: Int, heightMeasureSpec: Int, adapter: MityushkinLayoutAdapter, layout: MityushkinLayout): Array<Rect> {
        (adapter as YankinTemplatesAdapter).let {
            val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
            val firstLineChildWidth = (widthSize - 2 * it.marginBetweenViews) / 3
            val thirdLineChildWidth = (widthSize - 3 * it.marginBetweenViews) / 4

            val secondLineTop = it.threeAndMoreViewsHeight + it.marginBetweenViews
            val secondLineBottom = it.threeAndMoreViewsHeight + secondLineTop
            val thirdLineTop = secondLineBottom + it.marginBetweenViews
            val thirdLineBottom = thirdLineTop + it.threeAndMoreViewsHeight

            val secondImageLeft = firstLineChildWidth + it.marginBetweenViews
            val secondImageRight = firstLineChildWidth + secondImageLeft
            val thirdImageLeft = secondImageRight + it.marginBetweenViews
            val thirdImageRight = thirdImageLeft + firstLineChildWidth

            val eighthImageLeft = thirdLineChildWidth + it.marginBetweenViews
            val eighthImageRight = eighthImageLeft + thirdLineChildWidth
            val ninthImageLeft = eighthImageRight + it.marginBetweenViews
            val ninthImageRight = ninthImageLeft + thirdLineChildWidth
            val tenthImageLeft = ninthImageRight + it.marginBetweenViews
            val tenthImageRight = tenthImageLeft + thirdLineChildWidth

            val firstChild = layout.getChildAt(0)
            val secondChild = layout.getChildAt(1)
            val thirdChild = layout.getChildAt(2)
            val fourthChild = layout.getChildAt(3)
            val fifthChild = layout.getChildAt(4)
            val sixthChild = layout.getChildAt(5)
            val seventhChild = layout.getChildAt(6)
            val eighthChild = layout.getChildAt(7)
            val ninthChild = layout.getChildAt(8)
            val tenthChild = layout.getChildAt(9)

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
                sixthChild.bottomLeftCropRadius = it.childBorderRadius
                sixthChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (seventhChild is RoundedView) {
                seventhChild.topLeftCropRadius = it.childBorderRadius
                seventhChild.topRightCropRadius = it.childBorderRadius
                seventhChild.bottomLeftCropRadius = it.cornerBorderRadius
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
                ninthChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (tenthChild is RoundedView) {
                tenthChild.topLeftCropRadius = it.childBorderRadius
                tenthChild.topRightCropRadius = it.childBorderRadius
                tenthChild.bottomLeftCropRadius = it.childBorderRadius
                tenthChild.bottomRightCropRadius = it.cornerBorderRadius
            }

            return arrayOf(
                    Rect(0, 0, firstLineChildWidth, it.threeAndMoreViewsHeight),
                    Rect(secondImageLeft, 0, secondImageRight, it.threeAndMoreViewsHeight),
                    Rect(thirdImageLeft, 0, thirdImageRight, it.threeAndMoreViewsHeight),
                    Rect(0, secondLineTop, firstLineChildWidth, secondLineBottom),
                    Rect(secondImageLeft, secondLineTop, secondImageRight, secondLineBottom),
                    Rect(thirdImageLeft, secondLineTop, thirdImageRight, secondLineBottom),
                    Rect(0, thirdLineTop, thirdLineChildWidth, thirdLineBottom),
                    Rect(eighthImageLeft, thirdLineTop, eighthImageRight, thirdLineBottom),
                    Rect(ninthImageLeft, thirdLineTop, ninthImageRight, thirdLineBottom),
                    Rect(tenthImageLeft, thirdLineTop, tenthImageRight, thirdLineBottom)
            )
        }
    }
}