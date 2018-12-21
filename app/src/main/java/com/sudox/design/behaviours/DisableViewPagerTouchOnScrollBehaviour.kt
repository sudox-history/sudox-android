package com.sudox.design.behaviours

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import android.view.ViewConfiguration

class DisableViewPagerTouchOnScrollBehaviour : AppBarLayout.Behavior {

    private var pager: ViewPager? = null
    private var touchSlop = -1
    private var totalYConsumed = 0

    constructor() : super()
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onStartNestedScroll(parent: CoordinatorLayout,
                                     child: AppBarLayout,
                                     directTargetChild: View,
                                     target: View,
                                     nestedScrollAxes: Int,
                                     type: Int): Boolean {

        val scroll = super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type)

        if (scroll && directTargetChild is ViewPager) {
            pager = directTargetChild

            // Update touch slop
            if (touchSlop < 0) touchSlop = ViewConfiguration
                    .get(parent.context)
                    .scaledTouchSlop
        }

        return scroll
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout,
                                   child: AppBarLayout,
                                   target: View,
                                   dx: Int,
                                   dy: Int,
                                   consumed: IntArray,
                                   type: Int) {

        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)

        if (pager != null && consumed[1] > 0) {
            totalYConsumed += consumed[1]

            if (totalYConsumed > touchSlop) {
                pager!!.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, abl: AppBarLayout, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, abl, target, type)

        if (pager != null) {
            pager!!.requestDisallowInterceptTouchEvent(false);
            pager = null;
            totalYConsumed = 0;
        }
    }
}