package com.sudox.design.widgets.navbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.IntDef
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import com.sudox.design.R
import com.sudox.design.helpers.SANS_SERIF_NORMAL
import com.sudox.design.helpers.addCircleRipple

class NavigationBarButton : AppCompatTextView {

    @IconDirection
    private var iconDirection: Int = IconDirection.NAVIGATION_BAR_BUTTON_ICON_LEFT_DIRECTION
    private var iconDrawable: Drawable? = null
    private var iconTintColor: Int = 0
    private var iconMargin: Int = 0

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(IconDirection.NAVIGATION_BAR_BUTTON_ICON_LEFT_DIRECTION,
            IconDirection.NAVIGATION_BAR_BUTTON_ICON_RIGHT_DIRECTION)
    annotation class IconDirection {
        companion object {
            const val NAVIGATION_BAR_BUTTON_ICON_LEFT_DIRECTION = 0
            const val NAVIGATION_BAR_BUTTON_ICON_RIGHT_DIRECTION = 1
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.navigationBarBtnStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        with(context.obtainStyledAttributes(attrs, R.styleable.NavigationBarButton, defStyleAttr,
                R.style.NavigationBarStyle_Button)) {

            iconTintColor = getColor(R.styleable.NavigationBarButton_navbarBtnIconTint, 0)
            iconMargin = getDimensionPixelSize(R.styleable.NavigationBarButton_navbarBtnIconMargin, 0)
            gravity = Gravity.CENTER_VERTICAL
            typeface = SANS_SERIF_NORMAL

            setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.styleable.NavigationBarButton_navbarBtnTextSize, 0F))
            setTextColor(getColor(R.styleable.NavigationBarButton_navbarBtnTextColor, 0))
            addCircleRipple()
            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (canRenderIcon()) {
            if (!canRenderText()) {
                setMeasuredDimension(heightMeasureSpec, heightMeasureSpec)
            } else {
                measureComponents(widthMeasureSpec, heightMeasureSpec)
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    // Because it's function will be called only from onMeasure()
    @SuppressLint("WrongCall")
    private fun measureComponents(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Calculating text size ...
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val iconWidth = iconDrawable!!.intrinsicWidth
        val needWidth = iconWidth + iconMargin + measuredWidth
        val needWidthMeasureSpec = MeasureSpec.makeMeasureSpec(needWidth, MeasureSpec.EXACTLY)
        setMeasuredDimension(needWidthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        if (canRenderIcon()) {
            if (!canRenderText()) {
                renderIconInCenter(canvas)
            } else if (iconDirection == IconDirection.NAVIGATION_BAR_BUTTON_ICON_LEFT_DIRECTION) {
                renderComponentsInLeft(canvas)
            } else if (iconDirection == IconDirection.NAVIGATION_BAR_BUTTON_ICON_RIGHT_DIRECTION) {
                renderComponentsInRight(canvas)
            }
        } else {
            super.onDraw(canvas)
        }
    }

    private fun renderIconInCenter(canvas: Canvas) {
        val iconWidth = iconDrawable!!.intrinsicWidth
        val iconHeight = iconDrawable!!.intrinsicHeight
        val iconLeftBorder = (width / 2 - iconWidth / 2).toFloat()
        val iconBottomBorder = (height / 2 - iconHeight / 2).toFloat()

        canvas.translate(iconLeftBorder, iconBottomBorder)
        iconDrawable!!.draw(canvas)
    }

    // Because it's function will be called only from onDraw()
    @SuppressLint("WrongCall")
    private fun renderComponentsInLeft(canvas: Canvas) {
        val iconWidth = iconDrawable!!.intrinsicWidth
        val iconHeight = iconDrawable!!.intrinsicHeight
        val iconBottomBorder = (height / 2 - iconHeight / 2).toFloat()
        val textLeftBorder = (iconWidth + iconMargin).toFloat()

        canvas.save()
        canvas.translate(0F, iconBottomBorder)
        iconDrawable!!.draw(canvas)

        canvas.restore()
        canvas.translate(textLeftBorder, 0F)
        super.onDraw(canvas)
    }

    // Because it's function will be called only from onDraw()
    @SuppressLint("WrongCall")
    private fun renderComponentsInRight(canvas: Canvas) {
        val iconWidth = iconDrawable!!.intrinsicWidth
        val iconHeight = iconDrawable!!.intrinsicHeight
        val iconBottomBorder = (height / 2 - iconHeight / 2).toFloat()
        val iconLeftBorder = (width - iconWidth).toFloat()

        canvas.save()
        canvas.translate(iconLeftBorder, iconBottomBorder)
        iconDrawable!!.draw(canvas)

        canvas.restore()
        super.onDraw(canvas)
    }

    fun setIconDrawable(drawable: Drawable?) {
        iconDrawable = drawable
        iconDrawable?.setTint(iconTintColor)
        iconDrawable?.setBounds(0, 0, iconDrawable!!.intrinsicWidth, iconDrawable!!.intrinsicHeight)
    }

    fun setIconDrawableRes(@DrawableRes drawableRes: Int) {
        val drawable = ContextCompat.getDrawable(context, drawableRes)
        setIconDrawable(drawable)
    }

    fun setIconDirection(@IconDirection direction: Int) {
        iconDirection = direction
    }

    fun applyChanges() {
        requestLayout()
        invalidate()
    }

    private fun canRenderText(): Boolean {
        return !TextUtils.isEmpty(text)
    }

    private fun canRenderIcon(): Boolean {
        return iconDrawable != null
    }
}