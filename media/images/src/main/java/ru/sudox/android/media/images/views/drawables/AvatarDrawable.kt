package ru.sudox.android.media.images.views.drawables

import android.graphics.Canvas
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
) : BasicDrawable(imageView) {

    override fun draw(canvas: Canvas) {
        imageView.let {
            paint.color = ColorUtils.setAlphaComponent(it.avatarColor, alpha)
            super.draw(canvas)

            val textY = bounds.exactCenterY() - it.avatarTextBounds.exactCenterY()
            val textX = bounds.exactCenterX() - it.avatarTextBounds.exactCenterX()
            val color = it.avatarTextPaint.color

            it.avatarTextPaint.color = ColorUtils.setAlphaComponent(color, currentAlpha)
            canvas.drawText(it.textInAvatar!!, textX, textY, it.avatarTextPaint)
            it.avatarTextPaint.color = color
        }
    }
}