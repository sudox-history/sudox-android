package com.sudox.design.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View

class CorrectlyMeasuringTextView : PrecomputedTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (layout.lineCount <= 1)
            return

        // View width
        var maxWidth = 0

        for (i in layout.lineCount - 1 downTo 0) {
            maxWidth = Math.max(maxWidth, Math.round(
                    layout.paint.measureText(text, layout.getLineStart(i), layout.getLineEnd(i))))
        }

        super.onMeasure(Math.min(maxWidth + paddingLeft + paddingRight, measuredWidth)
                or View.MeasureSpec.EXACTLY, measuredHeight or View.MeasureSpec.EXACTLY)
    }
}