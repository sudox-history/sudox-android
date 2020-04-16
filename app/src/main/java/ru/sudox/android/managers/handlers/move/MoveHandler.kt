package ru.sudox.android.managers.handlers.move

import android.animation.Animator
import android.animation.ValueAnimator
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler

private const val MAX_TRANSLATION = 25F

abstract class MoveHandler : AnimatorChangeHandler {

    constructor() : super()
    constructor(removesFromViewOnPush: Boolean) : super(removesFromViewOnPush)
    constructor(duration: Long, removesFromViewOnPush: Boolean) : super(duration, removesFromViewOnPush)
    constructor(duration: Long) : super(duration)

    override fun getAnimator(container: ViewGroup, from: View?, to: View?, isPush: Boolean, toAddedToContainer: Boolean): Animator {
        val targetTranslation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MAX_TRANSLATION, container.resources.displayMetrics)

        return ValueAnimator
                .ofFloat(0F, 1.0F)
                .setDuration(animationDuration)
                .apply {
                    interpolator = LinearInterpolator()

                    addUpdateListener {
                        val progressAndAlpha = it.animatedValue as Float

                        from!!.alpha = 1 - progressAndAlpha
                        to!!.alpha = progressAndAlpha

                        onUpdate(from, to, progressAndAlpha, targetTranslation)
                    }
                }
    }

    override fun resetFromView(from: View) {
        from.translationY = 0F
        from.alpha = 1F
    }

    abstract fun onUpdate(from: View, to: View, progressAndAlpha: Float, targetTranslation: Float)
}