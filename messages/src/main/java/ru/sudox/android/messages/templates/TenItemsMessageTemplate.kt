package ru.sudox.android.messages.templates

import android.graphics.Rect
import android.view.View
import ru.sudox.design.mityushkinlayout.MityushkinLayout
import ru.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import ru.sudox.design.mityushkinlayout.MityushkinLayoutTemplate
import ru.sudox.design.common.views.RoundedView

object TenItemsMessageTemplate : MityushkinLayoutTemplate {

    override var dependsFromChildSize = false

    override fun layout(widthMeasureSpec: Int, heightMeasureSpec: Int, adapter: MityushkinLayoutAdapter, layout: MityushkinLayout): Array<Rect> {
        (adapter as MessageTemplatesAdapter).let {
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

            it.setCorners(layout)

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