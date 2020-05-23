package ru.sudox.android.messages.attachments

import ru.sudox.android.messages.R
import ru.sudox.design.mityushkinlayout.MityushkinLayout
import ru.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import ru.sudox.design.mityushkinlayout.MityushkinLayoutTemplate

class MessagesTemplatesAdapter : MityushkinLayoutAdapter {

    internal var alignToRight = false
    internal var childBorderRadius = 0F
    internal var cornerBorderRadius = 0F
    internal var marginBetweenViews = 0
    internal var maxSingleViewHeight = 0
    internal var minSingleViewHeight = 0
    internal var maxSingleViewWidth = 0
    internal var threeAndMoreViewsHeight = 0
    internal var twoViewsHeight = 0

    override fun onAttached(layout: MityushkinLayout) {
        layout.context.resources.apply {
            childBorderRadius = getDimensionPixelSize(R.dimen.messagestemplatesadapter_child_border_radius).toFloat()
            cornerBorderRadius = getDimensionPixelSize(R.dimen.messagestemplatesadapter_corner_border_radius).toFloat()
            marginBetweenViews = getDimensionPixelSize(R.dimen.messagestemplatesadapter_margin_between_views)
            maxSingleViewHeight = getDimensionPixelSize(R.dimen.messagestemplatesadapter_max_single_view_height)
            minSingleViewHeight = getDimensionPixelSize(R.dimen.messagestemplatesadapter_min_single_view_height)
            maxSingleViewWidth = getDimensionPixelSize(R.dimen.messagestemplatesadapter_max_single_view_width)
            threeAndMoreViewsHeight = getDimensionPixelSize(R.dimen.messagestemplatesadapter_three_and_more_views_height)
            twoViewsHeight = getDimensionPixelSize(R.dimen.messagestemplatesadapter_two_views_height)
        }
    }

    override fun getTemplate(count: Int): MityushkinLayoutTemplate? {
        return when (count) {
            1 -> SingleItemMessagesTemplate
            2 -> TwoItemsMessagesTemplate
            3 -> ThreeItemsMessagesTemplate
            4 -> FourItemsMessagesTemplate
            5 -> FiveItemsMessagesTemplate
            6 -> SixItemsMessagesTemplate
            7 -> SevenItemsMessagesTemplate
            8 -> EightItemsMessagesTemplate
            9 -> NineItemsMessagesTemplate
            10 -> TenItemsMessagesTemplate
            else -> null
        }
    }
}