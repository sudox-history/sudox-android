package ru.sudox.android.messages.views

import android.animation.Animator
import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.graphics.withTranslation
import androidx.core.widget.TextViewCompat.setTextAppearance
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.media.images.views.AvatarImageView
import ru.sudox.android.media.images.views.GlideImageView
import ru.sudox.android.messages.R
import ru.sudox.android.messages.vos.MessageVO
import ru.sudox.design.common.paint.DrawablePaint
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * View, отображающая лайки, поставленные на сообщение.
 *
 * Перед использованием убедитесь, что у родительской View установлен параметр clipChildren = false,
 * иначе возможна обрезка иконки, прикрепленной к аватарке.
 */
class MessageLikesView : ViewGroup {

    private var avatarPath = Path()
    private var likePaint = DrawablePaint()
    private var clipPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private var likeOutlinePaint = DrawablePaint()
    private var avatarsViews = ArrayList<AvatarImageView>()
    private var avatarsIndexes = HashMap<View, Int>()
    private var marginBetweenAvatarsAndCount = 0
    private var marginBetweenAvatars = 0
    private var counterWidthWhenFull = 0
    private var avatarHeight = 0
    private var avatarWidth = 0

    private var countTextView = AppCompatTextView(context).apply {
        visibility = View.GONE
        this@MessageLikesView.addView(this)
    }

    var vo: MessageVO? = null
        private set

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.messageLikesViewStyle)

    @SuppressLint("Recycle", "ObjectAnimatorBinding")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.MessageLikesView, defStyleAttr, 0).use {
            avatarWidth = it.getDimensionPixelSizeOrThrow(R.styleable.MessageLikesView_avatarWidth)
            avatarHeight = it.getDimensionPixelSizeOrThrow(R.styleable.MessageLikesView_avatarHeight)
            marginBetweenAvatars = it.getDimensionPixelSizeOrThrow(R.styleable.MessageLikesView_marginBetweenAvatars)
            marginBetweenAvatarsAndCount = it.getDimensionPixelSizeOrThrow(R.styleable.MessageLikesView_marginBetweenAvatarsAndCount)
            setTextAppearance(countTextView, it.getResourceIdOrThrow(R.styleable.MessageLikesView_countTextAppearance))

            likePaint.readFromTypedArray(typedArray = it,
                    drawableRes = R.styleable.MessageLikesView_likeIconDrawable,
                    heightRes = R.styleable.MessageLikesView_likeIconHeight,
                    widthRes = R.styleable.MessageLikesView_likeIconWidth,
                    tintColorRes = R.styleable.MessageLikesView_likeIconTint)

            likeOutlinePaint.readFromTypedArray(typedArray = it,
                    drawableRes = R.styleable.MessageLikesView_likeIconDrawable,
                    heightRes = R.styleable.MessageLikesView_likeIconHeight,
                    widthRes = R.styleable.MessageLikesView_likeIconWidth)

            val clipColor = it.getColorOrThrow(R.styleable.MessageLikesView_clipColor)
            val likeIconClipWidth = it.getDimensionPixelSizeOrThrow(R.styleable.MessageLikesView_likeIconClipWidth)

            likeOutlinePaint.width += likeIconClipWidth * 2
            likeOutlinePaint.height += likeIconClipWidth * 2
            likeOutlinePaint.tintColor = clipColor
            clipPaint.strokeWidth = marginBetweenAvatars.toFloat()
            clipPaint.color = clipColor
        }

        counterWidthWhenFull = with(Rect()) {
            countTextView.paint.getTextBounds("999+", 0, 4, this)
            width() + countTextView.paddingLeft + paddingRight
        }

        layoutTransition = LayoutTransition()
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        measureChild(countTextView, widthSpec, heightSpec)

        val avatarsWidth = (avatarsViews.sumBy {
            measureChild(it, widthSpec, heightSpec)
            it.measuredWidth
        } + getAvatarWidth()) / 2

        var needWidth = (avatarsViews.size - 1) * marginBetweenAvatars + avatarsWidth + paddingLeft + paddingRight
        var needHeight = paddingTop + paddingBottom

        if (countTextView.visibility == View.VISIBLE) {
            needWidth += marginBetweenAvatarsAndCount + countTextView.measuredWidth
            needHeight += max(getAvatarHeight(), countTextView.measuredHeight)
        } else {
            needHeight += getAvatarHeight()
        }

        setMeasuredDimension(needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val avatarTop = paddingTop
        val countTop = (measuredHeight - paddingTop) / 2 - countTextView.measuredHeight / 2
        val avatarBottom = avatarTop + getAvatarHeight()
        val countBottom = countTop + countTextView.measuredHeight

        if (vo?.isSentByMe == true) {
            var avatarRight = measuredWidth - paddingRight
            var avatarLeft = 0

            for (i in avatarsViews.lastIndex downTo 0) {
                val view = avatarsViews[i]

                avatarLeft = avatarRight - view.measuredWidth
                view.layout(avatarLeft, avatarTop, avatarRight, avatarBottom)
                avatarRight = avatarLeft + view.measuredWidth / 2 - marginBetweenAvatars
            }

            if (countTextView.visibility == View.VISIBLE) {
                val countRight = avatarLeft - marginBetweenAvatarsAndCount
                val countLeft = countRight - countTextView.measuredWidth

                countTextView.layout(countLeft, countTop, countRight, countBottom)
            }
        } else {
            var avatarLeft = paddingLeft
            var avatarRight = 0

            for (i in avatarsViews.lastIndex downTo 0) {
                val view = avatarsViews[i]

                avatarRight = avatarLeft + view.measuredWidth
                view.layout(avatarLeft, avatarTop, avatarRight, avatarBottom)
                avatarLeft = avatarRight - view.measuredWidth / 2 + marginBetweenAvatars
            }

            if (countTextView.visibility == View.VISIBLE) {
                val countLeft = avatarRight + marginBetweenAvatarsAndCount
                val countRight = countLeft + countTextView.measuredWidth

                countTextView.layout(countLeft, countTop, countRight, countBottom)
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        if (avatarsViews.isNotEmpty()) {
            val last = avatarsViews.last()
            val angle = if (vo?.isSentByMe == true) {
                Math.PI / 4
            } else {
                3 * Math.PI / 4
            }

            val pointX = (last.right + last.left) / 2F + (cos(angle) * (last.right - last.left) / 2F).toFloat()
            val pointY = (last.bottom + last.top) / 2F + (sin(angle) * (last.bottom - last.top) / 2F).toFloat()

            canvas.withTranslation(x = pointX - likeOutlinePaint.width / 2, y = pointY - likeOutlinePaint.height / 2) {
                likeOutlinePaint.draw(canvas)
            }

            canvas.withTranslation(x = pointX - likePaint.width / 2, y = pointY - likePaint.height / 2) {
                likePaint.draw(canvas)
            }
        }
    }

    /**
     * Выдает ширину, которая будет достигнута при установке более 3-х лайков.
     * По-другому, максимальная ширина, которую может достигнуть данная View.
     *
     * @return Ширина, которая будет достигнута при достижении порога 3-х лайков
     */
    fun getWidthWhenFull(): Int {
        return 2 * avatarWidth + 2 * marginBetweenAvatars + paddingLeft + paddingRight + counterWidthWhenFull + marginBetweenAvatarsAndCount
    }

    /**
     * Выставляет VO в данную View.
     * NB! Все изменения будут обработаны без анимаций.
     *
     * @param vo ViewObject с данными для отображения
     * @param glide Glide для загрузки изображений.
     */
    @SuppressLint("SetTextI18n")
    fun setVOs(vo: MessageVO?, glide: GlideRequests) {
        val likes = vo?.likes
        val likesCount = likes?.size ?: 0
        val firstLikesCount = min(likesCount, 3)
        val needRemove = min(this.vo?.likes?.size ?: 0, 3) - firstLikesCount

        if (needRemove > 0) {
            var animator: Animator? = null

            if (needRemove == avatarsViews.size) {
                animator = layoutTransition.getAnimator(LayoutTransition.CHANGE_DISAPPEARING)
                layoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, null)
            }

            repeat(needRemove) {
                val view = avatarsViews.removeAt(avatarsViews.size - 1)

                avatarsIndexes.remove(view)
                removeView(view)
            }

            if (animator != null) {
                layoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, animator)
            }
        } else if (needRemove < 0) {
            repeat(abs(needRemove)) {
                val view = createAvatarView()
                var animator: Animator? = null

                if (avatarsViews.isEmpty()) {
                    animator = layoutTransition.getAnimator(LayoutTransition.CHANGE_APPEARING)
                    layoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, null)
                }

                avatarsViews.add(view)
                view.maskCallbacks.push(::cropAvatar)
                addView(view)

                if (animator != null) {
                    layoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, animator)
                }
            }
        }

        if (likesCount > 3) {
            countTextView.visibility = View.VISIBLE
            countTextView.text = "+${min(likesCount - 3, 999)}"
        } else {
            countTextView.visibility = View.GONE
        }

        for (i in 0 until firstLikesCount) {
            val view = avatarsViews[i]

            avatarsIndexes[view] = i
            view.setVO(likes!![i], glide)
            view.translationZ = i.toFloat()
        }

        this.vo = vo
        requestLayout()
        invalidate()
    }

    private fun cropAvatar(view: GlideImageView, path: Path) {
        if (avatarsIndexes[view]!! != avatarsViews.lastIndex) {
            val centerY = view.measuredHeight / 2F
            val radius = (view as AvatarImageView).getRadius() + marginBetweenAvatars
            val centerX = if (vo?.isSentByMe == true) {
                view.measuredWidth + marginBetweenAvatars
            } else {
                -marginBetweenAvatars
            }.toFloat()

            avatarPath.reset()
            avatarPath.addCircle(centerX, centerY, radius, Path.Direction.CW)
            path.op(avatarPath, Path.Op.DIFFERENCE)
        }
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