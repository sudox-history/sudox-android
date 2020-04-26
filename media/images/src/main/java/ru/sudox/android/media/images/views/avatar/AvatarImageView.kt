package ru.sudox.android.media.images.views.avatar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getFloatOrThrow
import androidx.core.content.res.getFontOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.media.images.R
import ru.sudox.android.media.images.views.GlideCircleImageView
import ru.sudox.android.media.images.views.NOT_SHOWING_IMAGE_ID
import ru.sudox.android.media.texts.helpers.getTwoFirstLetters
import ru.sudox.design.appbar.AppBar
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sin

/**
 * ImageView для аватарок.
 *
 * Отображает первые буквы двух слов если пользователь не установил аватарку.
 * Также отображает кружок-индикатор статуса пользователя если это требует ViewObject.
 */
class AvatarImageView : GlideCircleImageView {

    private var indicatorNumberBounds = Rect()
    private var indicatorNumberText: String? = null
    private var indicatorNumberTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var indicatorClipRect = RectF()
    private var indicatorRect = RectF()

    private var avatarDrawable = AvatarDrawable(this)
    private var avatarColors: IntArray? = null

    internal var textInAvatar: String? = null
    internal var avatarTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    internal var avatarTextBounds = Rect()
    internal var avatarColor = 0

    var vo: AvatarVO? = null
        private set

    var avatarTextColor: Int
        get() = avatarTextPaint.color
        set(value) {
            avatarTextPaint.color = value
        }

    var avatarTextTypeface: Typeface?
        get() = avatarTextPaint.typeface
        set(value) {
            avatarTextPaint.typeface = value
        }

    var avatarHeightPercent = 0F
        set(value) {
            if (drawable != null) {
                requestLayout()
                invalidate()
            }

            field = value
        }

    var indicatorCropRadiusDiff = 0
        set(value) {
            field = value
            invalidate()
        }

    var indicatorCropColor = 0
        set(value) {
            field = value
            invalidate()
        }

    var indicatorRadius = 0
        set(value) {
            field = value
            invalidate()
        }

    var indicatorColor: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    var indicatorNumberTextColor: Int
        get() = indicatorNumberTextPaint.color
        set(value) {
            indicatorNumberTextPaint.color = value
            invalidate()
        }

    var indicatorNumberTextSize: Float
        get() = indicatorNumberTextPaint.textSize
        set(value) {
            indicatorNumberTextPaint.textSize = value

            requestLayout()
            invalidate()
        }

    var indicatorNumberTextTypeface: Typeface?
        get() = indicatorNumberTextPaint.typeface
        set(value) {
            indicatorNumberTextPaint.typeface = value

            requestLayout()
            invalidate()
        }

    var indicatorNumberTextPaddingTop = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var indicatorNumberTextPaddingBottom = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var indicatorNumberTextPaddingLeft = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var indicatorNumberTextPaddingRight = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.avatarImageViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.AvatarImageView, defStyleAttr, 0).use {
            indicatorNumberTextSize = it.getDimensionPixelSizeOrThrow(R.styleable.AvatarImageView_indicatorNumberTextSize).toFloat()
            indicatorNumberTextColor = it.getColorOrThrow(R.styleable.AvatarImageView_indicatorNumberTextColor)
            indicatorNumberTextTypeface = it.getFontOrThrow(R.styleable.AvatarImageView_indicatorNumberTextFontFamily)

            indicatorNumberTextPaddingTop = it.getDimensionPixelSize(R.styleable.AvatarImageView_indicatorNumberTextPaddingTop, 0)
            indicatorNumberTextPaddingBottom = it.getDimensionPixelSize(R.styleable.AvatarImageView_indicatorNumberTextPaddingBottom, 0)
            indicatorNumberTextPaddingLeft = it.getDimensionPixelSize(R.styleable.AvatarImageView_indicatorNumberTextPaddingLeft, 0)
            indicatorNumberTextPaddingRight = it.getDimensionPixelSize(R.styleable.AvatarImageView_indicatorNumberTextPaddingRight, 0)

            avatarColors = it.resources.getIntArray(it.getResourceIdOrThrow(R.styleable.AvatarImageView_avatarColors))
            avatarTextColor = it.getColorOrThrow(R.styleable.AvatarImageView_avatarTextColor)
            avatarTextTypeface = it.getFontOrThrow(R.styleable.AvatarImageView_avatarTextFontFamily)
            avatarHeightPercent = it.getFloatOrThrow(R.styleable.AvatarImageView_avatarHeightPercent)

            indicatorCropColor = it.getColorOrThrow(R.styleable.AvatarImageView_indicatorCropColor)
            indicatorCropRadiusDiff = it.getDimensionPixelSizeOrThrow(R.styleable.AvatarImageView_indicatorCropRadiusDiff)
            indicatorRadius = it.getDimensionPixelSizeOrThrow(R.styleable.AvatarImageView_indicatorRadius)
            indicatorColor = it.getColorOrThrow(R.styleable.AvatarImageView_indicatorColor)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (isIndicatorShowing()) {
            if (indicatorNumberText == null) {
                val centerX = cos(Math.PI / 4) * layoutParams.width / 2 + layoutParams.width / 2
                val centerY = sin(Math.PI / 4) * layoutParams.height / 2 + layoutParams.height / 2

                indicatorRect.right = (centerX + indicatorRadius).toFloat()
                indicatorRect.bottom = (centerY + indicatorRadius).toFloat()
                indicatorRect.left = (centerX - indicatorRadius).toFloat()
                indicatorRect.top = (centerY - indicatorRadius).toFloat()

                indicatorClipRect.left = indicatorRect.left - indicatorCropRadiusDiff
                indicatorClipRect.top = indicatorRect.top - indicatorCropRadiusDiff
                indicatorClipRect.right = indicatorRect.right + indicatorCropRadiusDiff
                indicatorClipRect.bottom = indicatorRect.bottom + indicatorCropRadiusDiff
            } else {
                indicatorNumberTextPaint.getTextBounds(indicatorNumberText, 0, indicatorNumberText!!.length, indicatorNumberBounds)

                indicatorRect.left = getImageWidth() / 2F
                indicatorRect.right = indicatorRect.left +
                        indicatorNumberTextPaddingLeft +
                        indicatorNumberBounds.width() +
                        indicatorNumberTextPaddingRight

                val indicatorHeight = indicatorNumberTextPaddingTop + indicatorNumberBounds.height() + indicatorNumberTextPaddingBottom

                indicatorRect.top = getImageHeight() - indicatorHeight / 2F
                indicatorRect.bottom = indicatorRect.top + indicatorHeight

                indicatorClipRect.left = indicatorRect.left - indicatorCropRadiusDiff
                indicatorClipRect.right = indicatorRect.right + indicatorCropRadiusDiff
                indicatorClipRect.bottom = indicatorRect.bottom + indicatorCropRadiusDiff
                indicatorClipRect.top = indicatorRect.top - indicatorCropRadiusDiff

                var needWidthSpec = widthMeasureSpec
                var needHeightSpec = heightMeasureSpec

                if (indicatorClipRect.bottom > getImageHeight()) {
                    needHeightSpec = MeasureSpec.makeMeasureSpec(indicatorClipRect.bottom.toInt(), MeasureSpec.EXACTLY)
                }

                if (indicatorClipRect.right > getImageWidth()) {
                    needWidthSpec = MeasureSpec.makeMeasureSpec(indicatorClipRect.right.toInt(), MeasureSpec.EXACTLY)
                }

                if (needWidthSpec != widthMeasureSpec || needHeightSpec != heightMeasureSpec) {
                    super.onMeasure(needWidthSpec, needHeightSpec)
                }
            }
        }

        if (textInAvatar != null) {
            avatarTextPaint.textSize = getImageHeight() * avatarHeightPercent
            avatarTextPaint.getTextBounds(textInAvatar, 0, textInAvatar!!.length, avatarTextBounds)
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.clipRect(paddingLeft, paddingTop, paddingRight + layoutParams.width, paddingTop + layoutParams.height)
        super.onDraw(canvas)
        canvas.restore()

        if (isIndicatorShowing()) {
            val rxY = min(indicatorRect.width(), indicatorRect.height()) / 2
            val indRxY = min(indicatorClipRect.width(), indicatorClipRect.height()) / 2

            indicatorPaint.color = indicatorCropColor
            canvas.drawRoundRect(indicatorClipRect, indRxY, indRxY, indicatorPaint)

            indicatorPaint.color = indicatorColor
            canvas.drawRoundRect(indicatorRect, rxY, rxY, indicatorPaint)

            if (indicatorNumberText != null) {
                val textY = indicatorRect.centerY() - indicatorNumberBounds.centerY()
                val textX = indicatorRect.centerX() - indicatorNumberBounds.centerX()

                canvas.drawText(indicatorNumberText!!, textX, textY, indicatorNumberTextPaint)
            }
        }
    }

    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        if (indicatorNumberText != null && parent is AppBar) {
            val offsetHorizontal = (indicatorClipRect.right.toInt() - getImageWidth())
            val offsetVertical = (measuredHeight - getImageHeight()) / 2

            super.layout(l + offsetHorizontal, t + offsetVertical, r + offsetHorizontal, b + offsetVertical)
        } else {
            super.layout(l, t, r, b)
        }
    }

    fun setVO(vo: AvatarVO?, glide: GlideRequests) {
        this.vo = vo

        if (vo != null) {
            val resourceId = vo.getResourceId()
            val number = vo.getNumberInIndicator()

            if (resourceId != NOT_SHOWING_IMAGE_ID) {
                loadImage(vo.getResourceId(), glide)
            } else {
                avatarColor = avatarColors!![(vo.getAvatarKey() % avatarColors!!.size).toInt()]
                textInAvatar = getTwoFirstLetters(vo.getTextInAvatar()!!)

                loadDrawable(glide, avatarDrawable)
            }

            indicatorNumberText = if (number == 0) {
                null
            } else if (number <= 9) {
                "$number"
            } else {
                "9+"
            }
        } else {
            indicatorNumberText = null
            cancelLoading(glide)
        }

        requestLayout()
        invalidate()
    }

    override fun getImageHeight(): Int {
        return layoutParams.height
    }

    override fun getImageWidth(): Int {
        return layoutParams.width
    }

    private fun isIndicatorShowing(): Boolean {
        return vo != null && vo!!.canShowIndicator()
    }
}