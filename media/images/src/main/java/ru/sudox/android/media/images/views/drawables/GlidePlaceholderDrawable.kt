package ru.sudox.android.media.images.views.drawables

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import ru.sudox.android.media.images.views.GlideImageView

class GlidePlaceholderDrawable(
        private val imageView: GlideImageView
) : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun draw(canvas: Canvas) {
        canvas.drawPath(imageView.clipPath, paint.apply {
            paint.color = imageView.placeholderColor
        })
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun setAlpha(alpha: Int) {
    }
}