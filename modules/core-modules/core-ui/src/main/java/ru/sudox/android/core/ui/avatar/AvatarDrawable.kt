package ru.sudox.android.core.ui.avatar

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import kotlin.math.min

/**
 * Drawable текстовой аватарки.
 *
 * @param text Текст по центру
 * @param backgroundColor Цвет фона
 * @param configureTextPaint Функция для конфигурирования отрисовки текста.
 */
class AvatarDrawable(
    private val text: String,
    @ColorInt private val backgroundColor: Int,
    configureTextPaint: (Paint) -> (Unit)
) : Drawable() {

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var currentBackgroundColor = backgroundColor
    private val textBounds = Rect()

    init {
        configureTextPaint(textPaint)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(currentBackgroundColor)
        canvas.drawText(
            text,
            bounds.exactCenterX() - textBounds.exactCenterX(),
            bounds.exactCenterY() - textBounds.exactCenterY(),
            textPaint
        )
    }

    override fun setAlpha(alpha: Int) {
        currentBackgroundColor = ColorUtils.setAlphaComponent(backgroundColor, alpha)
        textPaint.alpha = alpha
        invalidateSelf()
    }

    override fun onBoundsChange(bounds: Rect) {
        textPaint.textSize = min(bounds.width(), bounds.height()) * 0.3F
        textPaint.getTextBounds(text, 0, text.length, textBounds)
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE
    override fun setColorFilter(colorFilter: ColorFilter?) {}
}