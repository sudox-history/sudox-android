package com.sudox.design.mityushkinlayout.yankintemplates

import android.graphics.Rect
import android.view.View
import com.sudox.design.mityushkinlayout.MityushkinLayout
import com.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import com.sudox.design.mityushkinlayout.MityushkinLayoutTemplate

object SevenItemsYankinTemplate : MityushkinLayoutTemplate {

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