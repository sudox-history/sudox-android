package com.sudox.android.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.appbar.AppBarLayout
import com.sudox.android.R

class SearchAdditionalView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    var measureHeight: Int = 0
    lateinit var marginLayoutParams: MarginLayoutParams
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

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        measureHeight = MeasureSpec.getSize(heightMeasureSpec)

        // Hide this view if bottom padding is negative
        if (!visible) {
            marginLayoutParams = MarginLayoutParams(layoutParams)
            marginLayoutParams.bottomMargin = -measureHeight
        }
        layoutParams = AppBarLayout.LayoutParams(marginLayoutParams)
    }

    fun toggle() {
        TransitionManager.beginDelayedTransition(parent as ViewGroup, transitionSet)

        visible = if (!visible) {
            marginLayoutParams.setMargins(0, 0, 0, 0)
            true
        } else {
            marginLayoutParams.setMargins(0, 0, 0, -measureHeight)

            false
        }
        layoutParams = AppBarLayout.LayoutParams(marginLayoutParams)
    }
}