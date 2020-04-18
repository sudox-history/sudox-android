package ru.sudox.android.layouts

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

class AppBehavior : AppBarLayout.ScrollingViewBehavior() {

    private var bottomNavigationView: BottomNavigationView? = null

    override fun onMeasureChild(parent: CoordinatorLayout, child: View, parentWidthMeasureSpec: Int, widthUsed: Int, parentHeightMeasureSpec: Int, heightUsed: Int): Boolean {
        val usedHeight =  bottomNavigationView!!.measuredHeight
        val parentHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec) - usedHeight
        val validParentHeightSpec = View.MeasureSpec.makeMeasureSpec(parentHeight, View.MeasureSpec.EXACTLY)

        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, validParentHeightSpec, usedHeight)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (dependency is BottomNavigationView) {
            bottomNavigationView = dependency
            return true
        }

        return super.layoutDependsOn(parent, child, dependency)
    }
}