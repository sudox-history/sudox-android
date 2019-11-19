package com.sudox.messenger.android.core

import android.animation.Animator
import android.animation.AnimatorInflater
import androidx.fragment.app.Fragment

abstract class CoreFragment : Fragment(), Animator.AnimatorListener {

    private var animator: Animator? = null

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        if (enter && nextAnim != 0) {
            animator = AnimatorInflater.loadAnimator(context, nextAnim).apply {
                addListener(this@CoreFragment)
            }

            return animator
        }

        return super.onCreateAnimator(transit, enter, nextAnim)
    }

    override fun onAnimationCancel(animation: Animator) {
        animator?.removeListener(this)
    }

    override fun onAnimationEnd(animation: Animator) {
        animator?.removeListener(this)
    }

    override fun onDetach() {
        animator?.removeListener(this)
        super.onDetach()
    }

    override fun onAnimationRepeat(animation: Animator) {}
    override fun onAnimationStart(animation: Animator) {}
}