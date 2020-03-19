package com.sudox.design.mityushkinlayout.yankintemplates

import com.sudox.design.mityushkinlayout.MityushkinLayout
import com.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import com.sudox.design.mityushkinlayout.MityushkinLayoutTemplate
import com.sudox.design.mityushkinlayout.R

class YankinTemplatesAdapter : MityushkinLayoutAdapter {

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
            1 -> SingleItemYankinTemplate
            2 -> TwoItemsYankinTemplate
            3 -> ThreeItemsYankinTemplate
            4 -> FourItemsYankinTemplate
            5 -> FiveItemsYankinTemplate
            6 -> SixItemsYankinTemplate
            7 -> SevenItemsYankinTemplate
            8 -> EightItemsYankinTemplate
            9 -> NineItemsYankinTemplate
            10 -> TenItemsYankinTemplate
            else -> null
        }
    }
}