package ru.sudox.android.messages.templates

import android.graphics.Rect
import android.view.View
import ru.sudox.design.mityushkinlayout.MityushkinLayout
import ru.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import ru.sudox.design.mityushkinlayout.MityushkinLayoutTemplate

object NineItemsMessageTemplate : MityushkinLayoutTemplate {

    override var dependsFromChildSize = false

    override fun layout(widthMeasureSpec: Int, heightMeasureSpec: Int, adapter: MityushkinLayoutAdapter, layout: MityushkinLayout): Array<Rect> {
        (adapter as MessageTemplatesAdapter).let {
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

            it.setCorners(layout)

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