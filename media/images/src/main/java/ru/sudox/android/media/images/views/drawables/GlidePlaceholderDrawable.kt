package ru.sudox.android.media.images.views.drawables

import android.graphics.Canvas
import androidx.core.graphics.ColorUtils
import ru.sudox.android.media.images.views.GlideImageView

class GlidePlaceholderDrawable(
        private val imageView: GlideImageView
) : BasicDrawable(imageView) {

    override fun draw(canvas: Canvas) {
        paint.color = ColorUtils.setAlphaComponent(imageView.placeholderColor, alpha)
        super.draw(canvas)
    }
}