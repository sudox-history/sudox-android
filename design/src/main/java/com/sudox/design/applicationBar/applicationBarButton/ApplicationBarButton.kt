package com.sudox.design.applicationBar.applicationBarButton

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.VisibleForTesting
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.use
import com.sudox.design.R
import kotlin.math.min

class ApplicationBarButton : View {

    @VisibleForTesting
    var iconDrawable: Drawable? = null
    @VisibleForTesting
    var iconDrawableId = 0
    @VisibleForTesting
    var iconHeight = 0
    @VisibleForTesting
    var iconWidth = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.applicationBarButtonStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.ApplicationBarButton, defStyleAttr, 0).use {
            iconHeight = it.getDimensionPixelSizeOrThrow(R.styleable.ApplicationBarButton_iconHeight)
            iconWidth = it.getDimensionPixelSizeOrThrow(R.styleable.ApplicationBarButton_iconWidth)
        }

        toggle(null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        val needWidth = paddingLeft + (iconDrawable?.bounds?.width() ?: 0) + paddingRight
        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            availableWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            min(needWidth, availableWidth)
        } else {
            needWidth
        }

        val needHeight = paddingTop + (iconDrawable?.bounds?.height() ?: 0) + paddingBottom
        val measuredHeight = if (heightMode == MeasureSpec.EXACTLY) {
            availableHeight
        } else if (heightMode == MeasureSpec.AT_MOST) {
            min(needHeight, availableHeight)
        } else {
            needHeight
        }

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val state = parcelable as ApplicationBarButtonState

        state.apply {
            super.onRestoreInstanceState(superState)
            readToView(this@ApplicationBarButton)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        return ApplicationBarButtonState(superState!!).apply {
            writeFromView(this@ApplicationBarButton)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (iconDrawable == null) {
            return
        }

        val iconX = measuredWidth / 2 - iconDrawable!!.bounds.exactCenterX()
        val iconY = measuredHeight / 2 - iconDrawable!!.bounds.exactCenterY()
        canvas.translate(iconX, iconY)
        iconDrawable!!.draw(canvas)
    }

    fun toggle(iconDrawable: Drawable?, fromRes: Boolean = false) {
        this.iconDrawable = iconDrawable?.apply {
            setBounds(0, 0, iconWidth, iconHeight)
        }

        if (!fromRes) {
            iconDrawableId = 0
        }

        if (iconDrawable == null) {
            isClickable = false
            isFocusable = false
        } else {
            isClickable = true
            isFocusable = true
        }

        requestLayout()
        invalidate()
    }

    fun toggle(@DrawableRes iconDrawableId: Int) {
        val drawable = context.getDrawable(iconDrawableId)

        this.iconDrawableId = iconDrawableId
        this.toggle(drawable, true)
    }
}