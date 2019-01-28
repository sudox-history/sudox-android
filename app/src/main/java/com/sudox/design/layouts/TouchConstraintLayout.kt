package com.sudox.design.layouts

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.motion.MotionLayout
import android.support.v4.view.NestedScrollingParent2
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View

class TouchConstraintLayout : ConstraintLayout, NestedScrollingParent2 {

    // TouchConstraintLayout должен быть связан с MotionLayout'ом внутри себя
    var motionLayout: MotionLayout? = null
    var maxScrollY: Int = 0
    var totalScrollY: Int = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onStartNestedScroll(child: View, target: View, axis: Int, type: Int): Boolean {
        return axis == ViewCompat.SCROLL_AXIS_VERTICAL && motionLayout != null
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {}
    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        totalScrollY = Math.min(Math.max(totalScrollY + dyUnconsumed, 0), maxScrollY)

        // Calculate the animation progress
        val progress = totalScrollY.toFloat() / maxScrollY.toFloat()

        // Exclude freezes
        if (dyUnconsumed > 0 && progress < motionLayout!!.progress)
            return

        if (dyUnconsumed < 0 && progress > motionLayout!!.progress)
            return

        // Update the animation progress
        if (motionLayout!!.progress != progress) {
            motionLayout!!.progress = progress
        }
    }

    override fun onStopNestedScroll(target: View, type: Int) {}
    override fun onNestedScrollAccepted(child: View, target: View, axis: Int, type: Int) {}
}