package com.sudox.design.widgets.navbar.button

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.sudox.common.annotations.Checked
import com.sudox.design.helpers.addRipple
import com.sudox.design.helpers.isLayoutRtl

@SuppressLint("ViewConstructor")
class NavigationBarButton(context: Context, val params: NavigationBarButtonParams) : View(context) {

    internal var textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    internal var textBounds = Rect()
    internal var text: String? = null

    @NavigationBarButtonIconDirection
    internal var iconDirection: Int = NavigationBarButtonIconDirection.DEFAULT
    internal var iconDrawable: Drawable? = null

    init {
        textPaint.textSize = params.textSize
        textPaint.color = params.textColor
        textPaint.typeface = params.textTypeface
        addRipple()
        resetView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val needWidth = calculateWidth()
        val needWidthMeasureSpec = MeasureSpec.makeMeasureSpec(needWidth, MeasureSpec.EXACTLY)
        setMeasuredDimension(needWidthMeasureSpec, heightMeasureSpec)
    }

    internal fun calculateWidth(): Int {
        var needWidth = 0

        if (iconDrawable != null) {
            needWidth += iconDrawable!!.intrinsicWidth
        }

        if (text != null) {
            needWidth += textBounds.width()

            if (iconDrawable != null) {
                needWidth += params.iconTextMargin
            }
        }

        if (needWidth > 0) {
            needWidth += params.leftPadding + params.rightPadding
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
            canvas.width - params.rightPadding - iconDrawable!!.intrinsicWidth
        } else {
            params.leftPadding
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
            params.leftPadding + iconDrawable!!.intrinsicWidth + params.iconTextMargin
        } else {
            params.leftPadding
        }
    }

    internal fun isIconNeedShowOnRight(rtl: Boolean): Boolean {
        return (iconDirection == NavigationBarButtonIconDirection.END && !rtl) ||
                (iconDirection == NavigationBarButtonIconDirection.START && rtl)
    }

    fun setIconDrawable(drawable: Drawable?) {
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable?.setTint(params.iconTintColor)
        iconDrawable = drawable
        requestLayout()
        invalidate()
    }

    @Checked
    fun setIconDrawableRes(@DrawableRes drawableRes: Int) {
        val drawable = ContextCompat.getDrawable(context, drawableRes)
        setIconDrawable(drawable)
    }

    @Checked
    fun setIconDirection(@NavigationBarButtonIconDirection direction: Int) {
        iconDirection = direction
        invalidate()
    }

    fun setText(text: String?) {
        if (text != null) {
            textPaint.getTextBounds(text, 0, text.length, textBounds)
        }

        this.text = text
        requestLayout()
        invalidate()
    }

    @Checked
    fun setTextRes(@StringRes textRes: Int) {
        val text = resources.getString(textRes)
        setText(text)
    }

    @Checked
    fun resetView() {
        isClickable = false
        visibility = GONE
        setIconDirection(NavigationBarButtonIconDirection.DEFAULT)
        setIconDrawable(null)
        setText(null)
    }
}