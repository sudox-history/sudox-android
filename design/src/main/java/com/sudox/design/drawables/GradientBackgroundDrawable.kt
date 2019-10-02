package com.sudox.design.drawables

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Shader
import android.graphics.drawable.Drawable
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.use
import com.sudox.design.R

internal val GRADIENT_BACKGROUND_STYLE_ATTRS = intArrayOf(
        R.attr.backgroundGradientStartColor,
        R.attr.backgroundGradientEndColor
)

internal const val GRADIENT_BACKGROUND_START_COLOR_ATTR_INDEX = 0
internal const val GRADIENT_BACKGROUND_END_COLOR_ATTR_INDEX = 1

class GradientBackgroundDrawable(val context: Context) : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isDither = true
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)

        val height = right - left
        val width = bottom - top

        configureGradient(width, height)
    }

    @SuppressLint("Recycle")
    private fun configureGradient(width: Int, height: Int) {
        context.obtainStyledAttributes(GRADIENT_BACKGROUND_STYLE_ATTRS).use {
            val startColor = it.getColorOrThrow(GRADIENT_BACKGROUND_START_COLOR_ATTR_INDEX)
            val endColor = it.getColorOrThrow(GRADIENT_BACKGROUND_END_COLOR_ATTR_INDEX)

            paint.shader = LinearGradient(width.toFloat(), 0F, 0F, height.toFloat(), startColor, endColor, Shader.TileMode.CLAMP)
        }
    }

    override fun draw(canvas: Canvas) {
        val height = bounds.height().toFloat()
        val width = bounds.width().toFloat()

        canvas.drawRect(0F, 0F, width, height, paint)
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {}
    override fun setAlpha(alpha: Int) {}
}