package com.sudox.design.quiltview.patterns.impls

import com.sudox.design.quiltview.QuiltView
import com.sudox.design.quiltview.R
import com.sudox.design.quiltview.patterns.Pattern
import com.sudox.design.quiltview.patterns.PatternAdapter

class StandalonePatternAdapter : PatternAdapter {

    private val oneStandalonePattern = OneStandalonePattern()

    var marginBetweenViews = 0
    var minViewHeight = 0
    var maxViewHeight = 0
    var minViewWidth = 0
    var maxViewWidth = 0

    override fun onAttached(view: QuiltView) {
        view.context.resources.let {
            marginBetweenViews = it.getDimensionPixelSize(R.dimen.quiltview_margin_between_views)
            minViewHeight = it.getDimensionPixelSize(R.dimen.quiltview_min_view_height)
            maxViewHeight = it.getDimensionPixelSize(R.dimen.quiltview_max_view_height)
            minViewWidth = it.getDimensionPixelSize(R.dimen.quiltview_min_view_width)
            maxViewWidth = it.getDimensionPixelSize(R.dimen.quiltview_max_view_width)
        }
    }

    override fun getPattern(count: Int): Pattern? {
        return when(count) {
            1 -> oneStandalonePattern
            else -> null
        }
    }
}