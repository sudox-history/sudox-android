package com.sudox.design.quiltview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.sudox.design.quiltview.patterns.PatternAdapter
import com.sudox.design.quiltview.patterns.impls.StandalonePatternAdapter

class QuiltView : ViewGroup {

    var adapter: PatternAdapter? = null
        set(value) {
            field = value?.apply {
                onAttached(this@QuiltView)
            }

            requestLayout()
            invalidate()
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        adapter = StandalonePatternAdapter()
    }

    override fun addView(child: View, index: Int, params: LayoutParams) {
        super.addView(child, index, params.apply {
            height = LayoutParams.MATCH_PARENT
            width = LayoutParams.MATCH_PARENT
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom

        val unspecifiedWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        val unspecifiedHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

        for (i in 0 until childCount) {
            measureChild(getChildAt(i), unspecifiedWidthSpec, unspecifiedHeightSpec)
        }

        adapter?.getPattern(childCount)?.measure(widthSize, widthMode, heightSize, heightMode, adapter!!, this)?.let {
            setMeasuredDimension(it.first, it.second)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        adapter?.getPattern(childCount)?.layout(paddingLeft, paddingTop, adapter!!, this)
    }
}