package com.sudox.design.drawables.ripple

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.sqrt

class RippleMaskDrawable(@RippleMaskType var type: Int) : Drawable() {

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.color = Color.WHITE
    }

    override fun draw(canvas: Canvas) {
        val centerX = bounds.exactCenterX()
        val centerY = bounds.exactCenterY()
        val radius = getRadius()

        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    private fun getRadius(): Float {
        val max = max(bounds.width(), bounds.height())

        return if (type == RippleMaskType.BORDERED) {
            (max / 2).toFloat()
        } else if (type == RippleMaskType.BORDERLESS) {
            ceil(sqrt(max * max / 2F))
        } else {
            0F
        }
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
}