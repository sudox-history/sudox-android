package com.sudox.design.behaviours

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

class CollapsingProfileAdditionalBehaviour : CoordinatorLayout.Behavior<LinearLayout> {

    constructor() : super()
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: LinearLayout, dependency: View): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: LinearLayout, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            val layoutBottom = dependency.bottom
            val layoutTop = dependency.top

            // Update offset
            ViewCompat.offsetTopAndBottom(child, layoutBottom - layoutTop)

            // Notify, that view updated
            return true
        }

        // View not updated
        return false
    }
}
