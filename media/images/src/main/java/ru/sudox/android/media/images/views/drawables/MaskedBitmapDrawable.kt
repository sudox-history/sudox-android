package ru.sudox.android.media.images.views.drawables

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Shader
import android.graphics.drawable.Drawable
import ru.sudox.android.media.images.views.GlideImageView

class MaskedBitmapDrawable(
        private val bitmap: Bitmap,
        private val imageView: GlideImageView
) : Drawable() {

    private var path = Path()
    private var currentAlpha = 255
    private var corners = FloatArray(8)
    private var bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = bitmapShader
    }

    override fun draw(canvas: Canvas) {
        imageView.let {
            corners[0] = it.topLeftCropRadius
            corners[1] = it.topLeftCropRadius
            corners[2] = it.topRightCropRadius
            corners[3] = it.topRightCropRadius
            corners[4] = it.bottomRightCropRadius
            corners[5] = it.bottomRightCropRadius
            corners[6] = it.bottomLeftCropRadius
            corners[7] = it.bottomLeftCropRadius
        }

        path.reset()
        path.addRoundRect(0F, 0F, intrinsicWidth.toFloat(), intrinsicHeight.toFloat(), corners, Path.Direction.CW)

        canvas.drawPath(path, paint.apply {
            alpha = currentAlpha
        })
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
        currentAlpha = alpha
    }

    override fun getAlpha(): Int {
        return currentAlpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }
}