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
    }

    fun toggle() {
        TransitionManager.beginDelayedTransition(parent as ViewGroup, transitionSet)

        if (visibility == View.GONE) {
            visibility = View.VISIBLE
        } else if (visibility == View.VISIBLE) {
            visibility = View.GONE
        }
    }
}