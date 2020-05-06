package ru.sudox.android.messages.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.use
import androidx.core.graphics.withTranslation
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.media.images.views.AvatarImageView
import ru.sudox.android.messages.R
import ru.sudox.android.people.common.vos.PeopleVO
import ru.sudox.design.common.paint.DrawablePaint
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class MessageLikesView : ViewGroup {

    private var avatarWidth = 0
    private var avatarHeight = 0
    private var marginBetweenAvatars = 0
    private var avatarsViews = ArrayList<AvatarImageView>()
    private var clipPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private var likePaint = DrawablePaint()
    private var likeOutlinePaint = DrawablePaint()

    var vos: ArrayList<PeopleVO>? = null
        private set

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.messageLikesView)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.MessageLikesView, defStyleAttr, 0).use {
            avatarWidth = it.getDimensionPixelSizeOrThrow(R.styleable.MessageLikesView_avatarWidth)
            avatarHeight = it.getDimensionPixelSizeOrThrow(R.styleable.MessageLikesView_avatarHeight)
            marginBetweenAvatars = it.getDimensionPixelSizeOrThrow(R.styleable.MessageLikesView_marginBetweenAvatars)

            val clipColor = it.getColorOrThrow(R.styleable.MessageLikesView_clipColor)

            clipPaint.strokeWidth = marginBetweenAvatars.toFloat()
            clipPaint.color = clipColor

            likePaint.readFromTypedArray(typedArray = it,
                    drawableRes = R.styleable.MessageLikesView_likeIconDrawable,
                    heightRes = R.styleable.MessageLikesView_likeIconHeight,
                    widthRes = R.styleable.MessageLikesView_likeIconWidth,
                    tintColorRes = R.styleable.MessageLikesView_likeIconTint)

            likeOutlinePaint.apply {
                val avatarClipWidth = it.getDimensionPixelSizeOrThrow(R.styleable.MessageLikesView_avatarClipWidth)

                readFromTypedArray(typedArray = it,
                        drawableRes = R.styleable.MessageLikesView_likeIconDrawable,
                        heightRes = R.styleable.MessageLikesView_likeIconHeight,
                        widthRes = R.styleable.MessageLikesView_likeIconWidth)

                tintColor = clipColor
                height += avatarClipWidth * 2
                width += avatarClipWidth * 2
            }
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val avatarsWidth = (avatarsViews.sumBy {
            measureChild(it, widthSpec, heightSpec)
            it.measuredWidth
        } + getAvatarWidth()) / 2

        val needWidth = (avatarsViews.size - 1) * marginBetweenAvatars + avatarsWidth + paddingLeft + paddingRight
        val needHeight = getAvatarHeight() + paddingTop + paddingBottom

        setMeasuredDimension(needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val topBorder = paddingTop
        val bottomBorder = topBorder + getAvatarHeight()
        var rightBorder = measuredWidth - paddingRight
        var leftBorder: Int

        avatarsViews.forEach {
            leftBorder = rightBorder - it.measuredWidth
            it.layout(leftBorder, topBorder, rightBorder, bottomBorder)
            rightBorder = leftBorder + it.measuredWidth / 2 - marginBetweenAvatars
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        avatarsViews.forEachIndexed { index, it ->
            if (index == avatarsViews.lastIndex) {
                return@forEachIndexed
            } else if (index == 0) {
                val pointX = (it.right + it.left) / 2F + (cos(Math.PI / 4) * (it.right - it.left) / 2F).toFloat()
                val pointY = (it.bottom + it.top) / 2F + (sin(Math.PI / 4) * (it.bottom - it.top) / 2F).toFloat()

                canvas.withTranslation(x = pointX - likeOutlinePaint.width / 2, y = pointY - likeOutlinePaint.height / 2) {
                    likeOutlinePaint.draw(canvas)
                }

                canvas.withTranslation(x = pointX - likePaint.width / 2, y = pointY - likePaint.height / 2) {
                    likePaint.draw(canvas)
                }
            }

            val leftBorder = it.left.toFloat()
            val topBorder = it.top.toFloat() - clipPaint.strokeWidth / 2
            val rightBorder = it.right.toFloat()
            val bottomBorder = it.bottom.toFloat() + clipPaint.strokeWidth / 2

            canvas.drawArc(leftBorder, topBorder, rightBorder, bottomBorder, 90F, 180F, false, clipPaint)
        }
    }

    fun setVOs(vos: ArrayList<PeopleVO>?, glide: GlideRequests) {
        val needRemove = (this.vos?.size ?: 0) - (vos?.size ?: 0)

        if (needRemove > 0) {
            repeat(needRemove) {
                removeView(avatarsViews.removeAt(avatarsViews.size - 1))
            }
        } else if (needRemove < 0) {
            repeat(abs(needRemove)) {
                val view = createAvatarView()

                avatarsViews.add(view)
                addView(view, 0)
            }
        }

        vos?.forEachIndexed { index, it ->
            avatarsViews[index].setVO(it, glide)
        }

        this.vos = vos
        requestLayout()
        invalidate()
    }

    private fun createAvatarView(): AvatarImageView {
        return AvatarImageView(context).apply {
            layoutParams = LayoutParams(avatarWidth, avatarHeight)
        }
    }

    private fun getAvatarWidth(): Int {
        return avatarsViews.firstOrNull()?.measuredWidth ?: 0
    }

    private fun getAvatarHeight(): Int {
        return avatarsViews.firstOrNull()?.measuredHeight ?: 0
    }
}