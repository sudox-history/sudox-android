package com.sudox.android.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.sudox.android.R

class SearchAdditionalView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    var measureHeight: Int = 0
    var visible: Boolean = false

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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        measureHeight = MeasureSpec.getSize(heightMeasureSpec)

        // Hide this view if bottom padding is negative
        if (!visible) {
            setPadding(0, 0, 0, -measureHeight)
        }
    }

    fun toggle() {
        TransitionManager.beginDelayedTransition(parent as ViewGroup, transitionSet)

        visible = if (!visible) {
            setPadding(0, 0, 0, 0)
            true
        } else {
            setPadding(0, 0,0, -measureHeight)
            false
        }
    }
}