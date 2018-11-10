package com.sudox.design.behaviours

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator

class MessagesBehaviour(context: Context, attributeSet: AttributeSet)
    :CoordinatorLayout.Behavior<AppBarLayout>(context, attributeSet) {


    init {

    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        ViewCompat.animate(child)
                .translationY(child.height.toFloat())
                .setDuration(200)
                .interpolator = AccelerateInterpolator()
    }

}