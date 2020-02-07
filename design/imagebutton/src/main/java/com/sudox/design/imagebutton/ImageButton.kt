package com.sudox.design.imagebutton

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.use
import com.sudox.design.common.calculateViewSize

/**
 * ImageButton с реализованной возможностью изменения размера иконки.
 */
class ImageButton : View {

    var iconDrawable: Drawable? = null
        set(value) {
            field = value?.mutate()?.apply {
                setTint(iconTint)
                setBounds(0, 0, iconWidth, iconHeight)
            }
        }

    var iconHeight = 0
        set(value) {
            iconDrawable?.setBounds(0, 0, iconWidth, value)
            field = value
        }

    var iconWidth = 0
        set(value) {
            iconDrawable?.setBounds(0, 0, value, iconHeight)
            field = value
        }

    var iconTint = 0
        set(value) {
            iconDrawable?.setTint(value)
            field = value
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.customImageButtonStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.ImageButton, defStyleAttr, 0).use {
            iconDrawable = it.getDrawable(R.styleable.ImageButton_iconDrawable)
            iconHeight = it.getDimensionPixelSize(R.styleable.ImageButton_iconHeight, 0)
            iconWidth = it.getDimensionPixelSize(R.styleable.ImageButton_iconWidth, 0)
            iconTint = it.getColor(R.styleable.ImageButton_iconTint, 0)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val needWidth = paddingLeft + (iconDrawable?.bounds?.width() ?: 0) + paddingRight
        val needHeight = paddingTop + (iconDrawable?.bounds?.height() ?: 0) + paddingBottom

        setMeasuredDimension(
                calculateViewSize(widthMeasureSpec, needWidth),
                calculateViewSize(heightMeasureSpec, needHeight)
        )
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
}