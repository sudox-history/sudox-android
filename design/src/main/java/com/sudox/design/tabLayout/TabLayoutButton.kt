package com.sudox.design.tabLayout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import com.sudox.design.R
import kotlin.math.min

class TabLayoutButton : View {

    private var activeTextColor = 0
    private var defaultTextColor = 0

    private var textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textBounds = Rect()
    private var text: String? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.tabLayoutButtonStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.TabLayoutButton, defStyleAttr, 0).use {
            activeTextColor = it.getColorOrThrow(R.styleable.TabLayoutButton_activeTextColor)
            defaultTextColor = it.getColorOrThrow(R.styleable.TabLayoutButton_defaultTextColor)

            val typefaceId = it.getResourceIdOrThrow(R.styleable.TabLayoutButton_textTypeface)

            textPaint.textSize = it.getDimensionPixelSizeOrThrow(R.styleable.TabLayoutButton_textSize).toFloat()
            textPaint.typeface = ResourcesCompat.getFont(context, typefaceId)
        }

        setActive(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        val needWidth = paddingLeft + textBounds.width() + paddingRight
        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            availableWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            min(needWidth, availableWidth)
        } else {
            needWidth
        }

        val needHeight = paddingTop + textBounds.height() + paddingBottom
        val measuredHeight = if (heightMode == MeasureSpec.EXACTLY) {
            availableHeight
        } else if (heightMode == MeasureSpec.AT_MOST) {
            min(needHeight, availableHeight)
        } else {
            needHeight
        }

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        if (text != null) {
            val textX = measuredWidth / 2 - textBounds.exactCenterX()
            val textY = measuredHeight / 2 - textBounds.exactCenterY()

            canvas.drawText(text!!, textX, textY, textPaint)
        }
    }

    fun setActive(active: Boolean) {
        textPaint.color = if (active) {
            activeTextColor
        } else {
            defaultTextColor
        }

        invalidate()
    }

    fun getTextWidth(): Int {
        return textBounds.width()
    }

    fun getTextBottom(): Int {
        return height / 2 - textBounds.centerY()
    }

    fun setText(text: String?) {
        this.text = text

        if (text != null) {
            textPaint.getTextBounds(text, 0, text.length, textBounds)
        }

        requestLayout()
        invalidate()
    }
}