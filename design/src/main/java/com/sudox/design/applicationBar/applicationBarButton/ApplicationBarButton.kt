package com.sudox.design.applicationBar.applicationBarButton

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import com.sudox.design.R
import com.sudox.design.isLayoutRtl
import kotlin.math.max
import kotlin.math.min

class ApplicationBarButton : View {

    internal var textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    internal var textBounds = Rect()
    internal var textRes = 0
    internal var text: String? = null

    internal var iconDirection = ApplicationBarButtonIconDirection.START
    internal var iconDrawableRes = 0
    internal var iconDrawable: Drawable? = null
    internal var iconTextMargin = 0
    internal var iconHeight = 0
    internal var iconWidth = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.applicationBarButtonStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.ApplicationBarButton, defStyleAttr, 0).use {
            iconTextMargin = it.getDimensionPixelSize(R.styleable.ApplicationBarButton_iconMargin, 0)
            iconHeight = it.getDimensionPixelSizeOrThrow(R.styleable.ApplicationBarButton_iconHeight)
            iconWidth = it.getDimensionPixelSizeOrThrow(R.styleable.ApplicationBarButton_iconWidth)

            textPaint.color = it.getColorOrThrow(R.styleable.ApplicationBarButton_textColor)
            textPaint.textSize = it.getDimensionPixelSizeOrThrow(R.styleable.ApplicationBarButton_textSize).toFloat()

            val typefaceId = it.getResourceIdOrThrow(R.styleable.ApplicationBarButton_textTypeface)
            textPaint.typeface = ResourcesCompat.getFont(context, typefaceId)
        }

        reset()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        val needWidth = calculateWidth()
        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            availableWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            min(needWidth, availableWidth)
        } else {
            needWidth
        }

        val needHeight = paddingTop + max(iconDrawable?.bounds?.height() ?: 0, textBounds.height()) + paddingBottom
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

    internal fun calculateWidth(): Int {
        var needWidth = 0

        if (iconDrawable != null) {
            needWidth += iconDrawable!!.intrinsicWidth
        }

        if (text != null) {
            needWidth += textBounds.width()

            if (iconDrawable != null) {
                needWidth += iconTextMargin
            }
        }

        if (needWidth > 0) {
            needWidth += paddingRight + paddingLeft
        }

        return needWidth
    }

    override fun dispatchDraw(canvas: Canvas) {
        val isRtl = isLayoutRtl()

        if (iconDrawable != null) {
            drawIcon(canvas, isRtl)
        }

        if (text != null) {
            drawText(canvas, isRtl)
        }
    }

    internal fun drawIcon(canvas: Canvas, rtl: Boolean) {
        val bottomBorder = getIconBottomBorder(canvas)
        val leftBorder = getIconLeftBorder(canvas, rtl)

        canvas.save()
        canvas.translate(leftBorder.toFloat(), bottomBorder.toFloat())
        iconDrawable!!.draw(canvas)
        canvas.restore()
    }

    internal fun getIconBottomBorder(canvas: Canvas): Int {
        return canvas.height / 2 - iconDrawable!!.intrinsicHeight / 2
    }

    internal fun getIconLeftBorder(canvas: Canvas, rtl: Boolean): Int {
        return if (text != null && isIconNeedShowOnRight(rtl)) {
            canvas.width - paddingRight - iconDrawable!!.intrinsicWidth
        } else {
            paddingLeft
        }
    }

    internal fun drawText(canvas: Canvas, rtl: Boolean) {
        val bottomBorder = getTextBottomBorder(canvas)
        val leftBorder = getTextLeftBorder(rtl)

        canvas.drawText(text!!, leftBorder.toFloat(), bottomBorder.toFloat(), textPaint)
    }

    internal fun getTextBottomBorder(canvas: Canvas): Int {
        return canvas.height / 2 - textBounds.centerY()
    }

    internal fun getTextLeftBorder(rtl: Boolean): Int {
        return if (iconDrawable != null && !isIconNeedShowOnRight(rtl)) {
            paddingLeft + iconDrawable!!.intrinsicWidth + iconTextMargin
        } else {
            paddingLeft
        }
    }

    internal fun isIconNeedShowOnRight(rtl: Boolean): Boolean {
        return iconDirection == ApplicationBarButtonIconDirection.END && !rtl ||
                iconDirection == ApplicationBarButtonIconDirection.START && rtl
    }

    fun setIconDrawable(drawable: Drawable?, fromRes: Boolean = false) {
        drawable?.setBounds(0, 0, iconWidth, iconHeight)

        if (!fromRes) {
            iconDrawableRes = 0
        }

        iconDrawable = drawable
        requestLayout()
        invalidate()
    }

    fun setIconDrawable(@DrawableRes iconDrawableRes: Int) {
        if (this.iconDrawableRes != iconDrawableRes) {
            this.setIconDrawable(getDrawable(context, iconDrawableRes), true)
            this.iconDrawableRes = iconDrawableRes
        }
    }

    fun setIconDirection(direction: ApplicationBarButtonIconDirection) {
        if (iconDirection != direction) {
            iconDirection = direction
            invalidate()
        }
    }

    fun setText(text: String?, fromRes: Boolean = false) {
        if (text != null) {
            textPaint.getTextBounds(text, 0, text.length, textBounds)
        }

        this.text = text

        if (!fromRes) {
            textRes = 0
        }

        requestLayout()
        invalidate()
    }

    fun setText(@StringRes textRes: Int) {
        if (this.textRes != textRes) {
            this.setText(resources.getString(textRes), true)
            this.textRes = textRes
        }
    }

    fun reset() {
        isClickable = false
        visibility = GONE
        setIconDirection(ApplicationBarButtonIconDirection.START)
        setIconDrawable(null)
        setText(null)
    }
}