package com.sudox.android.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import androidx.transition.ChangeBounds
import androidx.transition.TransitionSet
import com.sudox.android.R

class SearchAdditionalView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    var measureHeight: Int = 0
    var visible: Boolean = false
    var animator = animate()
            .setStartDelay(0)
            .setDuration(200)

    init {
        inflate(context, R.layout.include_search_navbar_addition, this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        measureHeight = MeasureSpec.getSize(heightMeasureSpec)

        // Hide this view if bottom padding is negative
        if (!visible) {
            y = -measureHeight.toFloat()
        }
    }

    fun toggle() {
        visible = if (!visible) {
            animator.interpolator = DecelerateInterpolator()
            animator.translationY(0F)
            true
        } else {
            animator.interpolator = AccelerateInterpolator()
            animator.translationY(-measureHeight.toFloat())
            false
        }
    }
}