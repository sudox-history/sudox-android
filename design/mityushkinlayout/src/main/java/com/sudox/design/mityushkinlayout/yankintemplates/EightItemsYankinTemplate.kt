package com.sudox.design.mityushkinlayout.yankintemplates

import android.graphics.Rect
import android.view.View
import com.sudox.design.mityushkinlayout.MityushkinLayout
import com.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import com.sudox.design.mityushkinlayout.MityushkinLayoutTemplate

object EightItemsYankinTemplate : MityushkinLayoutTemplate {

    override fun layout(widthMeasureSpec: Int, heightMeasureSpec: Int, adapter: MityushkinLayoutAdapter, layout: MityushkinLayout): Array<Rect> {
        (adapter as YankinTemplatesAdapter).let {
            val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
            val firstLineChildWidth = (widthSize - it.marginBetweenViews) / 2
            val secondLineChildWidth = (widthSize - 2 * it.marginBetweenViews) / 3

            val secondLineTop = it.threeAndMoreViewsHeight + it.marginBetweenViews
            val secondLineBottom = it.threeAndMoreViewsHeight + secondLineTop
            val thirdLineTop = secondLineBottom + it.marginBetweenViews
            val thirdLineBottom = thirdLineTop + it.threeAndMoreViewsHeight

            val fourthImageLeft = secondLineChildWidth + it.marginBetweenViews
            val fourthImageRight = fourthImageLeft + secondLineChildWidth
            val fifthImageLeft = fourthImageRight + it.marginBetweenViews
            val fifthImageRight = fifthImageLeft + secondLineChildWidth

            return arrayOf(
                    Rect(0, 0, firstLineChildWidth, it.threeAndMoreViewsHeight),
                    Rect(widthSize - firstLineChildWidth, 0, widthSize, it.threeAndMoreViewsHeight),
                    Rect(0, secondLineTop, secondLineChildWidth, secondLineBottom),
                    Rect(fourthImageLeft, secondLineTop, fourthImageRight, secondLineBottom),
                    Rect(fifthImageLeft, secondLineTop, fifthImageRight, secondLineBottom),
                    Rect(0, thirdLineTop, secondLineChildWidth, thirdLineBottom),
                    Rect(fourthImageLeft, thirdLineTop, fourthImageRight, thirdLineBottom),
                    Rect(fifthImageLeft, thirdLineTop, fifthImageRight, thirdLineBottom)
            )
        }
    }
}