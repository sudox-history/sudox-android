package ru.sudox.android.messages.templates

import android.view.ViewGroup
import ru.sudox.design.common.views.RoundedView
import ru.sudox.design.mityushkinlayout.MityushkinLayout
import ru.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import ru.sudox.design.mityushkinlayout.MityushkinLayoutTemplate
import ru.sudox.design.mityushkinlayout.R

class MessageTemplatesAdapter : MityushkinLayoutAdapter {

    internal var topLeftCropRadius = 0F
    internal var topRightCropRadius = 0F
    internal var bottomRightCropRadius = 0F
    internal var bottomLeftCropRadius = 0F

    internal var marginBetweenViews = 0
    internal var maxSingleViewHeight = 0
    internal var minSingleViewHeight = 0
    internal var maxSingleViewWidth = 0
    internal var threeAndMoreViewsHeight = 0
    internal var twoViewsHeight = 0

    override fun onAttached(layout: MityushkinLayout) {
        layout.context.resources.apply {
            marginBetweenViews = getDimensionPixelSize(R.dimen.yankintemplatesadapter_margin_between_views)
            maxSingleViewHeight = getDimensionPixelSize(R.dimen.yankintemplatesadapter_max_single_view_height)
            minSingleViewHeight = getDimensionPixelSize(R.dimen.yankintemplatesadapter_min_single_view_height)
            maxSingleViewWidth = getDimensionPixelSize(R.dimen.yankintemplatesadapter_max_single_view_width)
            threeAndMoreViewsHeight = getDimensionPixelSize(R.dimen.yankintemplatesadapter_three_and_more_views_height)
            twoViewsHeight = getDimensionPixelSize(R.dimen.yankintemplatesadapter_two_views_height)
        }
    }

    override fun getTemplate(count: Int): MityushkinLayoutTemplate? {
        return when (count) {
            1 -> SingleItemMessageTemplate
            2 -> TwoItemsMessageTemplate
            3 -> ThreeItemsMessageTemplate
            4 -> FourItemsMessageTemplate
            5 -> FiveItemsMessageTemplate
            6 -> SixItemsMessageTemplate
            7 -> SevenItemsMessageTemplate
            8 -> EightItemsYankinTemplate
            9 -> NineItemsMessageTemplate
            10 -> TenItemsMessageTemplate
            else -> null
        }
    }

    fun setCorners(parent: ViewGroup) {
        for (i in 0 until parent.childCount) {
            (parent.getChildAt(i) as RoundedView).let {
                it.bottomLeftCropRadius = bottomLeftCropRadius
                it.bottomRightCropRadius = bottomRightCropRadius
                it.topRightCropRadius = topRightCropRadius
                it.topLeftCropRadius = topLeftCropRadius
            }
        }
    }
}