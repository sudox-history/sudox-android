package ru.sudox.android.media.images.views.avatar

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

class AvatarDrawable(
        val imageView: AvatarImageView
) : Drawable() {

    override fun draw(canvas: Canvas) {
        canvas.drawColor(imageView.avatarColor)

        val textY = bounds.exactCenterY() - imageView.avatarTextBounds.exactCenterY()
        val textX = bounds.exactCenterX() - imageView.avatarTextBounds.exactCenterX()

        canvas.drawText(imageView.textInAvatar!!, textX, textY, imageView.avatarTextPaint)
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun setAlpha(alpha: Int) {
    }
}