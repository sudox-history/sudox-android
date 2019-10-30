package com.sudox.design.percentLayouts

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.core.content.res.use
import androidx.core.view.updatePadding
import com.sudox.design.R

class PercentRelativeLayout : RelativeLayout {

    private var paddingTopPercent = 0F
    private var paddingBottomPercent = 0F
    private var paddingRightPercent = 0F
    private var paddingLeftPercent = 0F

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.PercentRelativeLayout).use {
            paddingTopPercent = it.getFloat(R.styleable.PercentRelativeLayout_paddingTopPercent, 0F)
            paddingBottomPercent = it.getFloat(R.styleable.PercentRelativeLayout_paddingBottomPercent, 0F)
            paddingRightPercent = it.getFloat(R.styleable.PercentRelativeLayout_paddingRightPercent, 0F)
            paddingLeftPercent = it.getFloat(R.styleable.PercentRelativeLayout_paddingLeftPercent, 0F)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        updatePadding(
                top = (measuredHeight * paddingTopPercent).toInt(),
                bottom = (measuredHeight * paddingBottomPercent).toInt(),
                right = (measuredWidth * paddingRightPercent).toInt(),
                left = (measuredWidth * paddingLeftPercent).toInt()
        )
    }
}