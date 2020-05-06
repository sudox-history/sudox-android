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
    private var likePaint = DrawablePaint()
    private var likeOutlinePaint = DrawablePaint()
    private var avatarsViews = ArrayList<AvatarImageView>()
    private var clipPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

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

            clipPaint.color = clipColor
            clipPaint.strokeWidth = marginBetweenAvatars.toFloat()

            likePaint.readFromTypedArray(
                    it,
                    R.styleable.MessageLikesView_likeIconDrawable,
                    R.styleable.MessageLikesView_likeIconHeight,
                    R.styleable.MessageLikesView_likeIconWidth,
                    R.styleable.MessageLikesView_likeIconTint
            )

            likeOutlinePaint.readFromTypedArray(
                    it,
                    R.styleable.MessageLikesView_likeIconDrawable,
                    R.styleable.MessageLikesView_likeIconHeight,
                    R.styleable.MessageLikesView_likeIconWidth,
                    0
            )

            val avatarClipWidth = it.getDimensionPixelSizeOrThrow(R.styleable.MessageLikesView_avatarClipWidth)

            likeOutlinePaint.tintColor = clipColor
            likeOutlinePaint.height += avatarClipWidth * 2
            likeOutlinePaint.width += avatarClipWidth * 2
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val needWidth = avatarsViews.sumBy {
            measureChild(it, widthSpec, heightSpec)
            it.measuredWidth / 2
        } + getAvatarWidth() / 2 +
                paddingRight +
                paddingLeft +
                (avatarsViews.size - 1) *
                marginBetweenAvatars

        val needHeight = getAvatarHeight() +
                paddingTop +
                paddingBottom

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
            }

            canvas.drawArc(
                    it.left.toFloat(),
                    it.top.toFloat() - clipPaint.strokeWidth / 2,
                    it.right.toFloat(),
                    it.bottom.toFloat() + clipPaint.strokeWidth / 2,
                    90F,
                    180F,
                    false,
                    clipPaint
            )

            if (index == 0) {
                val xRadius = (it.right - it.left) / 2F
                val xCenter = (it.right + it.left) / 2F
                val yRadius = (it.bottom - it.top) / 2F
                val yCenter = (it.bottom + it.top) / 2F

                val pointX = xCenter + (cos(Math.PI / 4) * xRadius).toFloat()
                val pointY =  yCenter + (sin(Math.PI / 4) * yRadius).toFloat()

                canvas.save()
                canvas.translate(pointX - likeOutlinePaint.width / 2, pointY - likeOutlinePaint.height / 2)
                likeOutlinePaint.draw(canvas)
                canvas.restore()

                canvas.save()
                canvas.translate(pointX - likePaint.width / 2, pointY - likePaint.height / 2)
                likePaint.draw(canvas)
                canvas.restore()
            }
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
        return avatarsViews.firstOrNull()?.measuredHeight ?: 0
    }

    private fun getAvatarHeight(): Int {
        return avatarsViews.firstOrNull()?.measuredHeight ?: 0
    }
}