package ru.sudox.android.media.images.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getFloatOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.graphics.withTranslation
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.media.images.R
import ru.sudox.android.media.images.views.drawables.AvatarDrawable
import ru.sudox.android.media.images.views.vos.AvatarVO
import ru.sudox.android.media.texts.helpers.getTwoFirstLetters
import ru.sudox.design.common.drawables.BadgeDrawable
import ru.sudox.design.common.getFontCompat
import kotlin.math.cos
import kotlin.math.sin

/**
 * ImageView для аватарок.
 *
 * Отображает первые буквы двух слов если пользователь не установил аватарку.
 * Также отображает кружок-индикатор статуса пользователя если это требует ViewObject.
 *
 * Если используете цифровой индикатор, то убедитесь, что у родительской View выставлен clipChildren = false
 */
class AvatarImageView : GlideCircleImageView {

    private var avatarHeightPercent = 0F
    private var badgeDrawable: BadgeDrawable? = null
    private var avatarDrawable = AvatarDrawable(this)
    private var avatarColors: IntArray? = null

    internal var textInAvatar: String? = null
    internal var avatarTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    internal var avatarTextBounds = Rect()
    internal var avatarColor = 0

    var vo: AvatarVO? = null
        private set

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.avatarImageViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.AvatarImageView, defStyleAttr, 0).use {
            avatarColors = it.resources.getIntArray(it.getResourceIdOrThrow(R.styleable.AvatarImageView_avatarColors))
            avatarTextPaint.color = it.getColorOrThrow(R.styleable.AvatarImageView_avatarTextColor)
            avatarTextPaint.typeface = it.getFontCompat(context, R.styleable.AvatarImageView_avatarTextFontFamily)
            avatarHeightPercent = it.getFloatOrThrow(R.styleable.AvatarImageView_avatarHeightPercent)
            badgeDrawable = BadgeDrawable(context, true, it.getColorOrThrow(R.styleable.AvatarImageView_badgeColor))
        }

        maskCallbacks.push { _, path ->
            if (isIndicatorShowing()) {
                val x = getBadgeX()
                val y = getBadgeY()
                val cropPath = badgeDrawable!!.cropPath

                cropPath.offset(x, y)
                path.op(cropPath, Path.Op.DIFFERENCE)
                cropPath.offset(-x, -y)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (textInAvatar != null) {
            avatarTextPaint.textSize = measuredHeight * avatarHeightPercent
            avatarTextPaint.getTextBounds(textInAvatar, 0, textInAvatar!!.length, avatarTextBounds)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isIndicatorShowing()) {
            canvas.withTranslation(x = getBadgeX(), y = getBadgeY()) {
                badgeDrawable!!.draw(canvas)
            }
        }
    }

    /**
     * Выставляет ViewObject в качестве поставщика данных.
     * Также начинает загрузку картинки с помощью Glide.
     *
     * @param vo VO аватарки для получения информации об её отображении.
     * @param glide Обьект Glide для загрузки картинки.
     */
    fun setVO(vo: AvatarVO?, glide: GlideRequests) {
        this.vo = vo

        if (vo != null) {
            val resourceId = vo.getResourceId()
            val number = vo.getNumberInIndicator()

            if (resourceId != NOT_SHOWING_IMAGE_ID) {
                loadImage(resourceId, glide)
            } else {
                avatarColor = avatarColors!![(vo.getAvatarKey() % avatarColors!!.size).toInt()]
                textInAvatar = getTwoFirstLetters(vo.getTextInAvatar()!!)

                loadDrawable(glide, avatarDrawable)
            }

            badgeDrawable!!.badgeText = if (number == 0) {
                null
            } else if (number <= 9) {
                "$number"
            } else {
                "9+"
            }
        } else {
            avatarColor = 0
            textInAvatar = null
            badgeDrawable!!.badgeText = null

            cancelLoading(glide)
        }

        requestLayout()
        invalidate()
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams) {
        // Да, так опять же не очень хорошо делать, но опять же, нужно было быстро выкрутиться из ситуации.
        // Будущему тим-лиду Android команды: проанализируйте и реорганизуйте систему аваток если это требуется.
        minimumHeight = if (params.height > 0) {
            params.height
        } else {
            0
        }

        minimumWidth = if (params.width > 0) {
            params.width
        } else {
            0
        }

        super.setLayoutParams(params)
    }

    private fun getBadgeX(): Float {
        return if (badgeDrawable!!.badgeText == null) {
            ((cos(Math.PI / 4) * layoutParams.width / 2 + layoutParams.width / 2) - badgeDrawable!!.bounds.exactCenterX()).toFloat()
        } else {
            measuredWidth / 2F
        }
    }

    private fun getBadgeY(): Float {
        return if (badgeDrawable!!.badgeText == null) {
            ((sin(Math.PI / 4) * layoutParams.height / 2 + layoutParams.height / 2) - badgeDrawable!!.bounds.exactCenterY()).toFloat()
        } else {
            measuredHeight - badgeDrawable!!.bounds.exactCenterY()
        }
    }

    private fun isIndicatorShowing(): Boolean {
        return vo != null && vo!!.canShowIndicator()
    }
}