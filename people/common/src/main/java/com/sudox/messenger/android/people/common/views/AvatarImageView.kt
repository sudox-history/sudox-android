package com.sudox.messenger.android.people.common.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.use
import com.sudox.messenger.android.images.views.LoadableCircleImageView
import com.sudox.messenger.android.people.common.R
import com.sudox.messenger.android.people.common.vos.PeopleVO

/**
 * Загружаемый ImageView для аватарки.
 */
class AvatarImageView : LoadableCircleImageView {

    private var indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var vo: PeopleVO? = null
        set(value) {
            field = value

            if (value != null) {
                startLoading(value.photoId)
            } else {
                cancelLoading()
            }

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
            indicatorRadius = it.getDimensionPixelSizeOrThrow(R.styleable.AvatarImageView_indicatorRadius)
            indicatorColor = it.getColorOrThrow(R.styleable.AvatarImageView_indicatorColor)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        if (vo?.isStatusAboutOnline() == false && vo?.isUserOnline() == true) {
            val indicatorCenterY = measuredHeight / 2F
            val indicatorCenterX = measuredWidth / 2F

            canvas.drawCircle(indicatorCenterX, indicatorCenterY, indicatorRadius.toFloat(), indicatorPaint)
        }
    }
}