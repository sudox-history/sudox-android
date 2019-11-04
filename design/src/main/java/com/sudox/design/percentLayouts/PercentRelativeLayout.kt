package com.sudox.design.percentLayouts

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.core.content.res.use
import androidx.core.view.updatePadding
import com.sudox.design.R

class PercentRelativeLayout : RelativeLayout {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val windowSize = Point()

    var paddingTopPercent = 0F
    var paddingBottomPercent = 0F
    var paddingRightPercent = 0F
    var paddingLeftPercent = 0F

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
        windowManager.defaultDisplay.getSize(windowSize)

        val windowHeight = windowSize.y
        val windowWidth = windowSize.x

        if (paddingTopPercent != 0.0F) {
            updatePadding(top = (windowHeight * paddingTopPercent).toInt())
        }

        if (paddingBottomPercent != 0.0F) {
            updatePadding(bottom = (windowHeight * paddingBottomPercent).toInt())
        }

        if (paddingRightPercent != 0.0F) {
            updatePadding(right = (windowWidth * paddingRightPercent).toInt())
        }

        if (paddingLeftPercent != 0.0F) {
            updatePadding(left = (windowWidth * paddingLeftPercent).toInt())
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}