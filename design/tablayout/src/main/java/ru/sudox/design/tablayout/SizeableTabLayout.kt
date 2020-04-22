package ru.sudox.design.tablayout

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.tabs.TabLayout

class SizeableTabLayout : TabLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(minimumHeight, MeasureSpec.EXACTLY))
    }
}