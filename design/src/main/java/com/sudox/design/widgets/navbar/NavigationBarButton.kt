package com.sudox.design.widgets.navbar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.IntDef
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.view.View
import com.sudox.design.R
import com.sudox.design.helpers.addCircleRipple
import com.sudox.design.helpers.loadTypeface

@SuppressLint("ViewConstructor")
class NavigationBarButton(context: Context, styleResourceId: Int) : View(context) {

    internal var textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    internal var textBounds = Rect()
    internal var text: String? = null

    @IconDirection
    internal var iconDirection: Int = IconDirection.DEFAULT
    internal var iconTintColor: Int = 0
    internal var iconTextMargin: Int = 0
    internal var iconDrawable: Drawable? = null

    companion object {
        // Warning! All attrs must be ordered by id.
        private val styleAttrs = intArrayOf(
                android.R.attr.textSize,
                android.R.attr.textStyle,
                android.R.attr.textColor,
                android.R.attr.paddingLeft,
                android.R.attr.paddingRight,
                android.R.attr.fontFamily,
                R.attr.iconTextMargin,
                R.attr.iconTintColor)

        private const val TEXT_SIZE_ATTR_INDEX = 0
        private const val TEXT_STYLE_ATTR_INDEX = 1
        private const val TEXT_COLOR_ATTR_INDEX = 2
        private const val PADDING_LEFT_ATTR_INDEX = 3
        private const val PADDING_RIGHT_ATTR_INDEX = 4
        private const val FONT_FAMILY_ATTR_INDEX = 5
        private const val ICON_TEXT_MARGIN_ATTR_INDEX = 6
        private const val ICON_TINT_COLOR_ATTR_INDEX = 7
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(IconDirection.LEFT, IconDirection.RIGHT)
    annotation class IconDirection {
        companion object {
            const val LEFT = 0
            const val RIGHT = 1
            const val DEFAULT = LEFT
        }
    }

    init {
        with(context.obtainStyledAttributes(styleResourceId, styleAttrs)) {
            readTextAttrs(this)
            readViewAttrs(this)
            recycle()
        }

        addCircleRipple()
        resetView()
    }

    @SuppressLint("ResourceType")
    internal fun readTextAttrs(typedArray: TypedArray) {
        textPaint.textSize = typedArray.getDimensionPixelSize(TEXT_SIZE_ATTR_INDEX, -1).toFloat()
        textPaint.color = typedArray.getColor(TEXT_COLOR_ATTR_INDEX, -1)

        val textStyle = typedArray.getInt(TEXT_STYLE_ATTR_INDEX, -1)
        val fontFamily = typedArray.getString(FONT_FAMILY_ATTR_INDEX)
        textPaint.typeface = loadTypeface(fontFamily!!, textStyle)
    }

    @SuppressLint("ResourceType")
    internal fun readViewAttrs(typedArray: TypedArray) {
        val paddingLeft = typedArray.getDimensionPixelSize(PADDING_LEFT_ATTR_INDEX, -1)
        val paddingRight = typedArray.getDimensionPixelSize(PADDING_RIGHT_ATTR_INDEX, -1)
        setPadding(paddingLeft, 0, paddingRight, 0)

        iconTintColor = typedArray.getColor(ICON_TINT_COLOR_ATTR_INDEX, -1)
        iconTextMargin = typedArray.getDimensionPixelSize(ICON_TEXT_MARGIN_ATTR_INDEX, -1)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val needWidth = measureVisibleComponents()
        val needWidthMeasureSpec = MeasureSpec.makeMeasureSpec(needWidth, MeasureSpec.EXACTLY)
        setMeasuredDimension(needWidthMeasureSpec, heightMeasureSpec)
    }

    internal fun measureVisibleComponents(): Int {
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
            needWidth += paddingLeft + paddingRight
        }

        return needWidth
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (iconDrawable != null) {
            drawIcon(canvas)
        }

        if (text != null) {
            drawText(canvas)
        }
    }

    internal fun drawIcon(canvas: Canvas) {
        val bottomBorder = getIconBottomBorder(canvas)
        val leftBorder = getIconLeftBorder(canvas)

        canvas.save()
        canvas.translate(leftBorder.toFloat(), bottomBorder.toFloat())
        iconDrawable!!.draw(canvas)
        canvas.restore()
    }

    internal fun getIconBottomBorder(canvas: Canvas): Int {
        return canvas.height / 2 - iconDrawable!!.intrinsicHeight / 2
    }

    internal fun getIconLeftBorder(canvas: Canvas): Int {
        return if (text != null && iconDirection == IconDirection.RIGHT) {
            canvas.width - paddingRight - iconDrawable!!.intrinsicWidth
        } else {
            paddingLeft
        }
    }

    internal fun drawText(canvas: Canvas) {
        val leftBorder = getTextLeftBorder()
        val bottomBorder = getTextBottomBorder(canvas)

        canvas.drawText(text!!, leftBorder.toFloat(), bottomBorder.toFloat(), textPaint)
    }

    internal fun getTextLeftBorder(): Int {
        return if (iconDrawable != null && iconDirection == IconDirection.LEFT) {
            paddingLeft + iconDrawable!!.intrinsicWidth + iconTextMargin
        } else {
            paddingLeft
        }
    }

    internal fun getTextBottomBorder(canvas: Canvas): Int {
        return canvas.height / 2 - textBounds.centerY()
    }

    fun setIconDrawable(drawable: Drawable?) {
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable?.setTint(iconTintColor)
        iconDrawable = drawable
        requestLayout()
        invalidate()
    }

    fun setIconDrawableRes(@DrawableRes drawableRes: Int) {
        val drawable = ContextCompat.getDrawable(context, drawableRes)
        setIconDrawable(drawable)
    }

    fun setIconDirection(@IconDirection direction: Int) {
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

    fun setTextRes(@StringRes textRes: Int) {
        val text = resources.getString(textRes)
        setText(text)
    }

    fun resetView() {
        isClickable = false
        visibility = GONE
        setIconDirection(IconDirection.DEFAULT)
        setIconDrawable(null)
        setText(null)
    }
}