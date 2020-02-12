package com.sudox.design.imageview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.use

/**
 * Более производительный и эффективный ImageView
 *
 * Поддерживает установки картинки по умолчанию, а также
 * анимации при их смене.
 */
open class ImageView : View {

    var bitmap: Bitmap? = null
        set(value) {
            val bitmapIsNotEqualed = bitmap != value

            if (field != null && !field!!.isRecycled) {
                field!!.recycle()
            }

            field = if (value != null && (value.height != layoutParams.height || value.width != layoutParams.width)) {
                val thumbnail = ThumbnailUtils.extractThumbnail(value, layoutParams.width, layoutParams.height)
                value.recycle()
                thumbnail
            } else {
                value
            }

            if (bitmapIsNotEqualed) {
                invalidate()
            }
        }

    var drawable: Drawable? = null
    var defaultDrawable: Drawable? = null
        set(value) {
            field = value

            if (bitmap == null) {
                invalidate()
            }
        }

    private var bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.customImageViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.ImageView, defStyleAttr, 0).use {
            defaultDrawable = it.getDrawable(R.styleable.ImageView_defaultDrawable) ?: ColorDrawable(Color.BLACK)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = layoutParams.width
        val heightSize = layoutParams.height

        drawable?.setBounds(0, 0, layoutParams.width, layoutParams.height)
        defaultDrawable!!.setBounds(0, 0, widthSize, heightSize)
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (drawable != null) {
            drawable!!.draw(canvas)
        } else if (bitmap != null) {
            canvas.drawBitmap(bitmap!!, 0F, 0F, bitmapPaint)
        } else {
            defaultDrawable!!.draw(canvas)
        }
    }

    /**
     * Устанавливает Drawable как картинку
     *
     * @param drawable Drawable для установки. Если равен null,
     * то будет установлена картинка по умолчанию
     * @param colorTint Оттенок иконки
     */
    fun setDrawable(drawable: Drawable?, colorTint: Int) {
        this.drawable = drawable?.mutate()?.apply {
            setTint(colorTint)
        }
    }
}