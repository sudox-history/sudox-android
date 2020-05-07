package ru.sudox.android.messages.templates

import android.graphics.Rect
import android.view.View
import ru.sudox.design.mityushkinlayout.MityushkinLayout
import ru.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import ru.sudox.design.mityushkinlayout.MityushkinLayoutTemplate
import ru.sudox.design.common.views.RoundedView

object FourItemsMessageTemplate : MityushkinLayoutTemplate {

    override var dependsFromChildSize = false

    override fun layout(widthMeasureSpec: Int, heightMeasureSpec: Int, adapter: MityushkinLayoutAdapter, layout: MityushkinLayout): Array<Rect> {
        (adapter as MessageTemplatesAdapter).let {
            val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
            val childWidth = (widthSize - 2 * it.marginBetweenViews) / 3

            val secondLineTop = it.threeAndMoreViewsHeight + it.marginBetweenViews
            val secondLineBottom = it.threeAndMoreViewsHeight + secondLineTop
            val thirdImageLeft = childWidth + it.marginBetweenViews
            val thirdImageRight = thirdImageLeft + childWidth
            val fourthImageLeft = thirdImageRight + it.marginBetweenViews
            val fourthImageRight = fourthImageLeft + childWidth

            it.setCorners(layout)

            return arrayOf(
                    Rect(0, 0, widthSize, it.threeAndMoreViewsHeight),
                    Rect(0, secondLineTop, childWidth, secondLineBottom),
                    Rect(thirdImageLeft, secondLineTop, thirdImageRight, secondLineBottom),
                    Rect(fourthImageLeft, secondLineTop, fourthImageRight, secondLineBottom)
            )
        }
    }
}