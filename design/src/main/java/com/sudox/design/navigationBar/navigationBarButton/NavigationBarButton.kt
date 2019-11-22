package com.sudox.design.navigationBar.navigationBarButton

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.graphics.withTranslation
import com.sudox.design.R
import kotlin.math.max
import kotlin.math.min

class NavigationBarButton : View {

    private var defaultContentColor = 0
    private var focusedContentColor = 0
    private var clicked = false

    private var iconWidth = 0
    private var iconHeight = 0
    private var iconDrawable: Drawable? = null

    private var titleBounds = Rect()
    private var titlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var title: String? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.navigationBarButtonStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.NavigationBarButton, defStyleAttr, 0).use {
            defaultContentColor = it.getColorOrThrow(R.styleable.NavigationBarButton_defaultContentColor)
            focusedContentColor = it.getColorOrThrow(R.styleable.NavigationBarButton_focusedContentColor)

            iconWidth = it.getDimensionPixelSizeOrThrow(R.styleable.NavigationBarButton_iconWidth)
            iconHeight = it.getDimensionPixelSizeOrThrow(R.styleable.NavigationBarButton_iconHeight)

            val typefaceId = it.getResourceIdOrThrow(R.styleable.NavigationBarButton_titleFont)

            titlePaint.textSize = it.getDimensionPixelSizeOrThrow(R.styleable.NavigationBarButton_titleTextSize).toFloat()
            titlePaint.typeface = ResourcesCompat.getFont(context, typefaceId)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        val needWidth = paddingLeft + max(titleBounds.width(), iconDrawable!!.bounds.width()) + paddingRight
        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            availableWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            min(needWidth, availableWidth)
        } else {
            needWidth
        }

        val needHeight = paddingTop + iconDrawable!!.bounds.height() + titleBounds.height() + paddingBottom
        val measuredHeight = if (heightMode == MeasureSpec.EXACTLY) {
            availableHeight
        } else if (heightMode == MeasureSpec.AT_MOST) {
            min(needHeight, availableHeight)
        } else {
            needHeight
        }

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val iconX = measuredWidth / 2 - iconDrawable!!.bounds.exactCenterX()

        canvas.withTranslation(x = iconX, y = paddingTop.toFloat()) {
            iconDrawable!!.draw(canvas)
        }

        val titleX = measuredWidth / 2 - titleBounds.exactCenterX()
        val titleY = measuredHeight - paddingBottom.toFloat()

        canvas.drawText(title!!, titleX, titleY, titlePaint)
    }

    fun set(titleId: Int, iconId: Int) {
        title = if (titleId != 0) {
            val text = resources.getString(titleId)
            titlePaint.getTextBounds(text, 0, text.length, titleBounds)
            text
        } else {
            null
        }

        iconDrawable = if (iconId != 0) {
            val drawable = getDrawable(context, iconId)!!.mutate()
            drawable.setBounds(0, 0, iconWidth, iconHeight)
            drawable
        } else {
            null
        }

        setClicked(false)
    }

    fun setClicked(clicked: Boolean) {
        this.clicked = clicked

        val color = if (clicked) {
            focusedContentColor
        } else {
            defaultContentColor
        }

        iconDrawable!!.setTint(color)
        titlePaint.color = color

        invalidate()
    }
}