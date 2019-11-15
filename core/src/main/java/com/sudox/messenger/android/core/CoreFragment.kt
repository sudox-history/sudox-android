package com.sudox.messenger.android.core

import android.animation.Animator
import android.animation.AnimatorInflater
import androidx.fragment.app.Fragment

abstract class CoreFragment(
        val withIntensiveWork: Boolean = false
) : Fragment(), Animator.AnimatorListener {

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        if (!withIntensiveWork || !enter) {
            return super.onCreateAnimator(transit, enter, nextAnim)
        }

        return AnimatorInflater.loadAnimator(context, nextAnim).apply {
            addListener(this@CoreFragment)
        }
    }

    override fun onAnimationRepeat(animation: Animator?) {}
    override fun onAnimationEnd(animation: Animator?) {}
    override fun onAnimationCancel(animation: Animator?) {}
    override fun onAnimationStart(animation: Animator?) {}
}