package ru.sudox.android.media.images.views.avatar

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.core.graphics.ColorUtils

class AvatarDrawable(
        val imageView: AvatarImageView
) : Drawable() {

    private var currentAlpha = 255

    override fun draw(canvas: Canvas) {
        canvas.drawColor(ColorUtils.setAlphaComponent(imageView.avatarColor, currentAlpha))

        val textY = bounds.exactCenterY() - imageView.avatarTextBounds.exactCenterY()
        val textX = bounds.exactCenterX() - imageView.avatarTextBounds.exactCenterX()
        val color = imageView.avatarTextPaint.color

        imageView.avatarTextPaint.color = ColorUtils.setAlphaComponent(color, currentAlpha)
        canvas.drawText(imageView.textInAvatar!!, textX, textY, imageView.avatarTextPaint)
        imageView.avatarTextPaint.color = color
    }

    override fun setAlpha(alpha: Int) {
        currentAlpha = alpha
    }

    override fun getAlpha(): Int {
        return currentAlpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }
}