package com.sudox.design.tablayout

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout

class SizeableTabLayout : TabLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(minimumHeight, MeasureSpec.EXACTLY))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        val indicatorView = getChildAt(0) as ViewGroup

        for (i in 0 until indicatorView.childCount) {
            val child = indicatorView.getChildAt(i) as ViewGroup
            val first = child.getChildAt(0)
            val second = child.getChildAt(1)

            if (first.top != 0) {
                first.offsetTopAndBottom(-first.top)
            }

            if (second.top != 0) {
                second.offsetTopAndBottom(-second.top)
            }
        }
    }
}