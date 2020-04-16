package ru.sudox.android.managers.handlers.move

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler

abstract class MoveHandler : AnimatorChangeHandler {

    constructor() : super()
    constructor(removesFromViewOnPush: Boolean) : super(removesFromViewOnPush)
    constructor(duration: Long, removesFromViewOnPush: Boolean) : super(duration, removesFromViewOnPush)
    constructor(duration: Long) : super(duration)

    override fun getAnimator(container: ViewGroup, from: View?, to: View?, isPush: Boolean, toAddedToContainer: Boolean): Animator {
        return ValueAnimator
                .ofFloat(0F, 1.0F)
                .setDuration(animationDuration)
                .apply {
                    addUpdateListener {
                        val progressAndAlpha = it.animatedValue as Float

                        from!!.alpha = 1 - progressAndAlpha
                        to!!.alpha = progressAndAlpha

                        onUpdate(from, to, progressAndAlpha)
                    }
                }
    }

    override fun resetFromView(from: View) {
        from.translationY = 0F
        from.alpha = 1F
    }

    abstract fun onUpdate(from: View, to: View, progressAndAlpha: Float)
}