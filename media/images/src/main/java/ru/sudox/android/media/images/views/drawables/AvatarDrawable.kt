package ru.sudox.android.media.images.views.drawables

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.core.graphics.ColorUtils
import ru.sudox.android.media.images.views.AvatarImageView

/**
 * Drawable аватарки.
 * Отображает фон, выбранный ImageView и текст по середине.
 *
 * @param imageView Зависимая ImageView.
 */
class AvatarDrawable(
        private val imageView: AvatarImageView
) : Drawable() {

    private var currentAlpha = 255
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun draw(canvas: Canvas) {
        imageView.let {
            canvas.drawPath(it.clipPath, paint.apply {
                color = ColorUtils.setAlphaComponent(it.avatarColor, currentAlpha)
            })

            val textY = bounds.exactCenterY() - it.avatarTextBounds.exactCenterY()
            val textX = bounds.exactCenterX() - it.avatarTextBounds.exactCenterX()
            val color = it.avatarTextPaint.color

            it.avatarTextPaint.color = ColorUtils.setAlphaComponent(color, currentAlpha)
            canvas.drawText(it.textInAvatar!!, textX, textY, it.avatarTextPaint)
            it.avatarTextPaint.color = color
        }
    }

    override fun getAlpha(): Int {
        return currentAlpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setAlpha(alpha: Int) {
        currentAlpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }
}