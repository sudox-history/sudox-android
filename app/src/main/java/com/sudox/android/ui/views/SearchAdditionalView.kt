package com.sudox.android.ui.views

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import com.sudox.android.R
import com.sudox.android.common.helpers.hideKeyboard

class SearchAdditionalView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    var startListener: ((Boolean) -> Unit)? = null
    private var visible: Boolean = false
    private var animator = animate()
            .setStartDelay(0)
            .setDuration(300)

    init {
        inflate(context, R.layout.include_search_navbar_addition, this)
        animator.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {
                startListener?.invoke(visible)

                focusedChild?.clearFocus()
                hideKeyboard(context, this@SearchAdditionalView)
            }
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Hide this view if bottom padding is negative
        if (!visible) {
            translationY = -height.toFloat()
        }
    }

    fun toggle() {
        visible = if (!visible) {
            animator.interpolator = DecelerateInterpolator()
            animator.translationY(0F)
            true
        } else {
            animator.interpolator = AccelerateInterpolator()
            animator.translationY(-height.toFloat())
            false
        }
    }
}