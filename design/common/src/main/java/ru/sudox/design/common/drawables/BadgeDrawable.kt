package ru.sudox.design.common.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.res.ResourcesCompat.getFont
import ru.sudox.design.resources.R
import kotlin.math.min

class BadgeDrawable(
        context: Context,
        private val enableCropping: Boolean,
        badgeColor: Int
) : Drawable() {

    val path = Path()
    val cropPath = Path()

    private var radius = 0
    private var paddingVertical = 0
    private var paddingHorizontal = 0
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textBounds = Rect()
    private var cropWidth = 0F

    var badgeText: String? = null
        set(value) {
            field = value

            if (value != null) {
                textPaint.getTextBounds(value, 0, value.length, textBounds)
            }

            measureSelf()
            invalidateSelf()
        }

    init {
        radius = context.resources.getDimensionPixelSize(R.dimen.badgepaint_radius)
        paddingVertical = context.resources.getDimensionPixelSize(R.dimen.badgepaint_text_padding_vertical)
        paddingHorizontal = context.resources.getDimensionPixelSize(R.dimen.badgepaint_text_padding_horizontal)
        cropWidth = context.resources.getDimensionPixelSize(R.dimen.badgepaint_crop_width).toFloat()

        textPaint.color = getColor(context, R.color.badgepaint_text_color)
        textPaint.textSize = context.resources.getDimensionPixelSize(R.dimen.badgepaint_text_size).toFloat()
        textPaint.typeface = getFont(context, R.font.opensans_regular)
        paint.color = badgeColor

        measureSelf()
    }

    private fun measureSelf() {
        if (badgeText != null) {
            bounds.bottom = 2 * paddingVertical + textBounds.height()
            bounds.right = 2 * paddingHorizontal + textBounds.width()
        } else {
            bounds.bottom = radius * 2
            bounds.right = radius * 2
        }

        val width = bounds.width().toFloat()
        val height = bounds.height().toFloat()
        val radius = min(width / 2F, height / 2F)

        path.reset()
        path.addRoundRect(0F, 0F, width, height, radius, radius, Path.Direction.CW)

        if (enableCropping) {
            val cropRadius = radius + cropWidth

            cropPath.reset()
            cropPath.addRoundRect(-cropWidth, -cropWidth, width + cropWidth, height + cropWidth, cropRadius, cropRadius, Path.Direction.CW)
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(path, paint)

        if (badgeText != null) {
            canvas.drawText(badgeText!!, paddingHorizontal.toFloat(), bounds.height().toFloat() - paddingVertical, textPaint)
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getAlpha(): Int {
        return paint.alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {}
}