package ru.sudox.android.media.images.views.drawables

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import ru.sudox.android.media.images.views.GlideImageView

abstract class BasicDrawable(
        private val imageView: GlideImageView
) : Drawable() {

    var path = Path()
        private set
    var corners = FloatArray(8)
        private set
    var currentAlpha = 255
        private set
    var paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private set

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

        canvas.drawPath(path.apply {
            reset()
            addRoundRect(0F, 0F, bounds.width().toFloat(), bounds.height().toFloat(), corners, Path.Direction.CW)
        }, paint)
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setAlpha(alpha: Int) {
        currentAlpha = alpha
    }

    override fun getAlpha(): Int {
        return currentAlpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {}
}