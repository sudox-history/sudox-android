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
        val width = bounds.width().toFloat()
        val height = bounds.height().toFloat()

        imageView.let {
            val xK = width / it.measuredWidth.toFloat()
            val yK = height / it.measuredHeight.toFloat()

            corners[0] = it.topLeftCropRadius * xK
            corners[1] = it.topLeftCropRadius * yK
            corners[2] = it.topRightCropRadius * xK
            corners[3] = it.topRightCropRadius * yK
            corners[4] = it.bottomRightCropRadius * xK
            corners[5] = it.bottomRightCropRadius * yK
            corners[6] = it.bottomLeftCropRadius * xK
            corners[7] = it.bottomLeftCropRadius * yK
        }

        canvas.drawPath(path.apply {
            reset()
            addRoundRect(0F, 0F, width, height, corners, Path.Direction.CW)
            imageView.maskCallbacks.forEach { it(imageView, this) }
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