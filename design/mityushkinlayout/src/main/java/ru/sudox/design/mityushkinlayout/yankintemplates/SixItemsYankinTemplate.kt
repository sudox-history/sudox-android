package ru.sudox.design.mityushkinlayout.yankintemplates

import android.graphics.Rect
import android.view.View
import ru.sudox.design.mityushkinlayout.MityushkinLayout
import ru.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import ru.sudox.design.mityushkinlayout.MityushkinLayoutTemplate
import ru.sudox.design.roundedview.RoundedView

object SixItemsYankinTemplate : MityushkinLayoutTemplate {

    override var dependsFromChildSize = false

    override fun layout(widthMeasureSpec: Int, heightMeasureSpec: Int, adapter: MityushkinLayoutAdapter, layout: MityushkinLayout): Array<Rect> {
        (adapter as YankinTemplatesAdapter).let {
            val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
            val firstLineChildWidth = (widthSize - it.marginBetweenViews) / 2
            val secondLineChildWidth = (widthSize - 3 * it.marginBetweenViews) / 4

            val secondLineTop = it.threeAndMoreViewsHeight + it.marginBetweenViews
            val secondLineBottom = it.threeAndMoreViewsHeight + secondLineTop
            val thirdImageLeft = secondLineChildWidth + it.marginBetweenViews
            val thirdImageRight = thirdImageLeft + secondLineChildWidth
            val fourthImageLeft = thirdImageRight + it.marginBetweenViews
            val fourthImageRight = fourthImageLeft + secondLineChildWidth
            val fiveImageLeft = fourthImageRight + it.marginBetweenViews
            val fiveImageRight = fiveImageLeft + secondLineChildWidth

            val firstChild = layout.getChildAt(0)
            val secondChild = layout.getChildAt(1)
            val thirdChild = layout.getChildAt(2)
            val fourthChild = layout.getChildAt(3)
            val fifthChild = layout.getChildAt(4)
            val sixthChild = layout.getChildAt(5)

            if (firstChild is RoundedView) {
                firstChild.topLeftCropRadius = it.childBorderRadius
                firstChild.topRightCropRadius = it.childBorderRadius
                firstChild.bottomLeftCropRadius = it.childBorderRadius
                firstChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (secondChild is RoundedView) {
                secondChild.topLeftCropRadius = it.childBorderRadius
                secondChild.topRightCropRadius = it.cornerBorderRadius
                secondChild.bottomLeftCropRadius = it.childBorderRadius
                secondChild.bottomRightCropRadius = it.childBorderRadius
            }

            if (thirdChild is RoundedView) {
                thirdChild.topLeftCropRadius = it.childBorderRadius
                thirdChild.topRightCropRadius = it.childBorderRadius
                thirdChild.bottomLeftCropRadius = it.cornerBorderRadius
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
                sixthChild.bottomRightCropRadius = it.cornerBorderRadius
            }

            return arrayOf(
                    Rect(0, 0, firstLineChildWidth, it.threeAndMoreViewsHeight),
                    Rect(widthSize - firstLineChildWidth, 0, widthSize, it.threeAndMoreViewsHeight),
                    Rect(0, secondLineTop, secondLineChildWidth, secondLineBottom),
                    Rect(thirdImageLeft, secondLineTop, thirdImageRight, secondLineBottom),
                    Rect(fourthImageLeft, secondLineTop, fourthImageRight, secondLineBottom),
                    Rect(fiveImageLeft, secondLineTop, fiveImageRight, secondLineBottom)
            )
        }
    }
}