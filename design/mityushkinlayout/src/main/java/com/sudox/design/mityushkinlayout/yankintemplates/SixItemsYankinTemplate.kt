package com.sudox.design.mityushkinlayout.yankintemplates

import android.graphics.Rect
import android.view.View
import com.sudox.design.mityushkinlayout.MityushkinLayout
import com.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import com.sudox.design.mityushkinlayout.MityushkinLayoutTemplate

object SixItemsYankinTemplate : MityushkinLayoutTemplate {

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