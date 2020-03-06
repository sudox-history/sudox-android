package com.sudox.messenger.android.people.common.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.use
import com.sudox.messenger.android.images.views.GlideCircleImageView
import com.sudox.messenger.android.people.common.R
import com.sudox.messenger.android.people.common.vos.PeopleVO

/**
 * ImageView для аватарок.
 *
 * Отображает первые буквы двух слов если пользователь не установил аватарку.
 * Также отображает кружок-индикатор статуса пользователя если это требует ViewObject.
 */
class AvatarImageView : GlideCircleImageView {

    private var indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var vo: PeopleVO? = null
        set(value) {
            field = value

            if (value != null) {
                loadImage(value.photoId)
            } else {
                cancelLoading()
            }
        }

    var indicatorCropRadius = 0
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

    var indicatorColor = 0
        set(value) {
            indicatorPaint.color = value
            field = value
            invalidate()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.avatarImageViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.AvatarImageView, defStyleAttr, 0).use {
            indicatorCropColor = it.getColorOrThrow(R.styleable.AvatarImageView_indicatorCropColor)
            indicatorCropRadius = it.getDimensionPixelSizeOrThrow(R.styleable.AvatarImageView_indicatorCropRadius)
            indicatorRadius = it.getDimensionPixelSizeOrThrow(R.styleable.AvatarImageView_indicatorRadius)
            indicatorColor = it.getColorOrThrow(R.styleable.AvatarImageView_indicatorColor)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        if (vo?.isStatusAboutOnline() == false && vo?.isUserOnline() == true) {
            val rightBorder = measuredWidth.toFloat()
            val bottomBorder = measuredHeight.toFloat()
            val cropCenterX = rightBorder - indicatorCropRadius
            val cropCenterY = bottomBorder - indicatorCropRadius

            indicatorPaint.color = indicatorCropColor
            canvas.drawCircle(cropCenterX, cropCenterY, indicatorCropRadius.toFloat(), indicatorPaint)

            indicatorPaint.color = indicatorColor
            canvas.drawCircle(cropCenterX, cropCenterY, indicatorRadius.toFloat(), indicatorPaint)
        }
    }
}