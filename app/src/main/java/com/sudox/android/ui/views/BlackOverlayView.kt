package com.sudox.android.ui.views

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View

class BlackOverlayView : View {

    private val showAnimator = ObjectAnimator
            .ofFloat(this, "alpha", 0.4F)
            .setDuration(300)

    private val hideAnimator = ObjectAnimator
            .ofFloat(this, "alpha", 0F)
            .setDuration(300)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setBackgroundColor(Color.BLACK)
        isClickable = true
        visibility = View.GONE
        alpha = 0F

        showAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {
                visibility = View.VISIBLE
            }
        })

        hideAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                visibility = View.GONE
            }
        })
    }

    fun toggle(toggle: Boolean) {
        if (toggle) {
            showAnimator.end()
            hideAnimator.start()
        } else {
            hideAnimator.cancel()
            showAnimator.start()
        }
    }
}