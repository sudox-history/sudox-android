package ru.sudox.android.media.images.views.drawables

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import ru.sudox.android.media.images.views.GlideImageView

class MaskedBitmapDrawable(
        private val bitmap: Bitmap,
        private val imageView: GlideImageView
) : Drawable() {

    private var bitmapShader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = bitmapShader
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(imageView.clipPath, paint)
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    override fun getIntrinsicWidth(): Int {
        return bitmap.width
    }

    override fun getIntrinsicHeight(): Int {
        return bitmap.height
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }
}