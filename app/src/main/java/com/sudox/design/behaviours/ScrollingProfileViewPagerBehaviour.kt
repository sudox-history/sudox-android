package com.sudox.design.behaviours

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

class ScrollingProfileViewPagerBehaviour : CoordinatorLayout.Behavior<ViewPager> {

    constructor() : super()
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: ViewPager, dependency: View): Boolean {
        return dependency is LinearLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: ViewPager, dependency: View): Boolean {
        if (dependency is LinearLayout) {
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