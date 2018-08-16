package com.sudox.android.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import androidx.transition.Visibility
import com.sudox.android.R

class SearchAdditionalView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    private val transitionSet: TransitionSet by lazy {
        val transitionSet = TransitionSet()

        with(transitionSet) {
            addTransition(ChangeBounds())
            interpolator = DecelerateInterpolator()
            duration = 200
        }

        transitionSet
    }

    init {
        inflate(context, R.layout.include_search_navbar_addition, this)
        visibility = View.GONE
    }

    fun toggle() {
        TransitionManager.beginDelayedTransition(parent as ViewGroup, transitionSet)

        if (layoutParams.height == 0 || visibility == View.GONE) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            visibility = View.VISIBLE
        } else {
            layoutParams.height = 0
            visibility = View.INVISIBLE
        }

        // Update view sizes
        layoutParams = layoutParams
    }
}