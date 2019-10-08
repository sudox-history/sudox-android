package com.sudox.design.button

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.use
import com.sudox.design.R
import com.sudox.design.outline.OffsettedOutlineView
import com.sudox.design.outline.offsettedOutlineProvider

class SudoxButton : AppCompatButton, OffsettedOutlineView {

    private var topOutlineOffset = 0
    private var bottomOutlineOffset = 0
    private var leftOutlineOffset = 0
    private var rightOutlineOffset = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.buttonStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.SudoxButton, defStyleAttr, 0).use {
            topOutlineOffset = it.getDimensionPixelSize(R.styleable.SudoxButton_topOutlineOffset, 0)
            bottomOutlineOffset = it.getDimensionPixelSize(R.styleable.SudoxButton_bottomOutlineOffset, 0)
            leftOutlineOffset = it.getDimensionPixelSize(R.styleable.SudoxButton_leftOutlineOffset, 0)
            rightOutlineOffset = it.getDimensionPixelSize(R.styleable.SudoxButton_rightOutlineOffset, 0)
        }
    }

    init {
        outlineProvider = offsettedOutlineProvider
    }

    override fun getTopOutlineOffset(): Int {
        return topOutlineOffset
    }

    override fun getBottomOutlineOffset(): Int {
        return bottomOutlineOffset
    }

    override fun getLeftOutlineOffset(): Int {
        return leftOutlineOffset
    }

    override fun getRightOutlineOffset(): Int {
        return rightOutlineOffset
    }
}