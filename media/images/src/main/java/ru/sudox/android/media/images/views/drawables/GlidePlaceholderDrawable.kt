package ru.sudox.android.media.images.views.drawables

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.core.graphics.ColorUtils
import ru.sudox.android.media.images.views.GlideImageView

class GlidePlaceholderDrawable(
        private val imageView: GlideImageView
) : Drawable() {

    private var path = Path()
    private var corners = FloatArray(8)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var currentAlpha = 255

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
        path.addRoundRect(0F, 0F, bounds.width().toFloat(), bounds.height().toFloat(), corners, Path.Direction.CW)

        canvas.drawPath(path, paint.apply {
            paint.color = ColorUtils.setAlphaComponent(imageView.placeholderColor, currentAlpha)
        })
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