package com.sudox.design.imageview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.getIntegerOrThrow
import androidx.core.content.res.use

/**
 * Кастомный ImageView с реализованной анимацией смены картинки (только для Bitmap'ов).
 * Также автоматически утилизирует старый Bitmap, а также текущий если ImageView удаляется
 */
open class ImageView : View {

    private var oldBitmap: Bitmap? = null
    private var bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var oldBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var changingAnimator = ValueAnimator.ofInt(0, 255).apply {
        addUpdateListener {
            bitmapPaint.alpha = animatedValue as Int
            invalidate()
        }
    }

    var bitmap: Bitmap? = null
        set(value) {
            oldBitmap = field
            field = value

            if (field != null) {
                drawable = null
            }

            post { changingAnimator.start() }
        }

    var drawable: Drawable? = null
        set(value) {
            field = value

            if (field != null) {
                bitmap = null
            }

            postInvalidate()
        }

    var defaultDrawable: Drawable? = null
        set(value) {
            field = value

            if (bitmap == null && drawable == null) {
                postInvalidate()
            }
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.customImageViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.ImageView, defStyleAttr, 0).use {
            changingAnimator.duration = it.getIntegerOrThrow(R.styleable.ImageView_changingAnimationDuration).toLong()
            defaultDrawable = it.getDrawable(R.styleable.ImageView_defaultDrawable)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        layoutParams.let {
            defaultDrawable?.setBounds(0, 0, it.width, it.height)
            drawable?.setBounds(0, 0, it.width, it.height)
            setMeasuredDimension(it.width, it.height)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (drawable != null) {
            drawable!!.draw(canvas)
            return
        }

        if (bitmapPaint.alpha != 255 || bitmap == null || oldBitmapPaint.alpha != 255 || oldBitmap == null) {
            defaultDrawable!!.draw(canvas)
        }

        if (oldBitmap != null) {
            canvas.drawBitmap(oldBitmap!!, 0F, 0F, oldBitmapPaint)
        }

        if (bitmap != null) {
            canvas.drawBitmap(bitmap!!, 0F, 0F, bitmapPaint)
        }
    }

    /**
     * Устанавливает Drawable как картинку.
     * Внимание! Анимация изменения картинки не работает для Drawable.
     *
     * @param drawable Drawable для установки. Если равен null, то будет установлена картинка по умолчанию
     * @param colorTint Оттенок иконки
     */
    fun setDrawable(drawable: Drawable?, colorTint: Int) {
        this.drawable = drawable?.mutate()?.apply {
            setTint(colorTint)
        }
    }
}