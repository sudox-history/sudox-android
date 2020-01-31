package com.sudox.design.resizableImageButton

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.use
import com.sudox.design.R
import kotlin.math.min

class ResizableImageButton : View {

    internal var iconDrawableRes = 0

    private var iconDrawable: Drawable? = null
    private var iconDrawableTint = 0
    private var iconHeight = 0
    private var iconWidth = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.ResizableImageButton, defStyleAttr, 0).use {
            iconWidth = it.getDimensionPixelSize(R.styleable.ResizableImageButton_iconWidth, 0)
            iconHeight = it.getDimensionPixelSize(R.styleable.ResizableImageButton_iconHeight, 0)
            iconDrawableTint = it.getColor(R.styleable.ResizableImageButton_iconDrawableTint, 0)

            setIconDrawable(it.getDrawable(R.styleable.ResizableImageButton_iconDrawable))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        val needWidth = paddingLeft + iconDrawable!!.bounds.width() + paddingRight
        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            availableWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            min(needWidth, availableWidth)
        } else {
            needWidth
        }

        val needHeight = paddingTop + iconDrawable!!.bounds.height() + paddingBottom
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
        if (iconDrawable != null) {
            val iconX = canvas.width / 2 - iconDrawable!!.bounds.width() / 2F
            val iconY = canvas.height / 2 - iconDrawable!!.bounds.height() / 2F

            canvas.save()
            canvas.translate(iconX, iconY)
            iconDrawable!!.draw(canvas)
            canvas.restore()
        }
    }

    fun setIconDrawable(drawable: Drawable?, fromRes: Boolean = false) {
        drawable?.setBounds(0, 0, iconWidth, iconHeight)
        drawable?.setTint(iconDrawableTint)

        if (!fromRes) {
            iconDrawableRes = 0
        }

        iconDrawable = drawable
        requestLayout()
        invalidate()
    }

    fun setIconDrawable(@DrawableRes iconDrawableRes: Int) {
        if (this.iconDrawableRes != iconDrawableRes) {
            this.setIconDrawable(AppCompatResources.getDrawable(context, iconDrawableRes), true)
            this.iconDrawableRes = iconDrawableRes
        }
    }
}