package com.sudox.messenger.android.core

import android.animation.Animator
import android.animation.AnimatorInflater
import androidx.fragment.app.Fragment
import com.sudox.design.hideSoftKeyboard

abstract class CoreFragment : Fragment(), Animator.AnimatorListener {

    private var animator: Animator? = null

    override fun onResume() {
        if (!isHidden) {
            onHiddenChanged(false)
        }

        super.onResume()
    }

    override fun onDetach() {
        animator?.removeListener(this)
        onHiddenChanged(true)
        super.onDetach()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden) {
            activity!!.hideSoftKeyboard()
        }

        super.onHiddenChanged(hidden)
    }

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

    override fun onAnimationRepeat(animation: Animator) {}
    override fun onAnimationStart(animation: Animator) {}
}