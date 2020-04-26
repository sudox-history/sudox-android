package ru.sudox.design.roundedview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * ImageView, способная обрезать контент за пределами границ определенной фигуры
 */
open class RoundedImageView : AppCompatImageView, RoundedView {

    private val clipPath = Path()
    private val imagePaint = Paint()
    private val mergePaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT) }
    private val cropPaint = Paint().apply {
        color = Color.TRANSPARENT
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    override var topLeftCropRadius = 0F
        set(value) {
            if (value == field) {
                return
            }

            field = value
            requestLayout()
            invalidate()
        }

    override var topRightCropRadius = 0F
        set(value) {
            if (value == field) {
                return
            }

            field = value
            requestLayout()
            invalidate()
        }

    override var bottomLeftCropRadius = 0F
        set(value) {
            if (value == field) {
                return
            }

            field = value
            requestLayout()
            invalidate()
        }

    override var bottomRightCropRadius = 0F
        set(value) {
            if (value == field) {
                return
            }

            field = value
            requestLayout()
            invalidate()
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val right = getImageWidth().toFloat()
        val bottom = getImageHeight().toFloat()
        val radii = floatArrayOf(
                topLeftCropRadius,
                topLeftCropRadius,
                topRightCropRadius,
                topRightCropRadius,
                bottomRightCropRadius,
                bottomRightCropRadius,
                bottomLeftCropRadius,
                bottomLeftCropRadius
        )

        if (topLeftCropRadius != 0F || topRightCropRadius != 0F || bottomLeftCropRadius != 0F || bottomRightCropRadius != 0F) {
            clipPath.let {
                it.reset()
                it.addRoundRect(0F, 0F, right, bottom, radii, Path.Direction.CW)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (topLeftCropRadius != 0F || topRightCropRadius != 0F || bottomLeftCropRadius != 0F || bottomRightCropRadius != 0F) {
            val imageLayer = canvas.saveLayer(0F, 0F, getImageWidth().toFloat(), getImageHeight().toFloat(), imagePaint)

            drawable?.setBounds(0, 0, getImageWidth(), getImageHeight())
            drawable?.draw(canvas)

            val mergeLayer = canvas.saveLayer(0F, 0F, getImageWidth().toFloat(), getImageHeight().toFloat(), mergePaint)

            canvas.drawColor(Color.GREEN)
            canvas.drawPath(clipPath, cropPaint)
            canvas.restoreToCount(mergeLayer)
            canvas.restoreToCount(imageLayer)
        } else {
            super.onDraw(canvas)
        }
    }

    open fun getImageHeight(): Int {
        return measuredHeight
    }

    open fun getImageWidth(): Int {
        return measuredWidth
    }
}