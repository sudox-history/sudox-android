package ru.sudox.android.messages.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getFloatOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.graphics.withTranslation
import androidx.core.widget.TextViewCompat.setTextAppearance
import ru.sudox.android.media.MediaAttachmentsLayout
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.media.texts.LinkifiedTextView
import ru.sudox.android.messages.R
import ru.sudox.android.messages.attachments.MessagesTemplatesAdapter
import ru.sudox.android.messages.vos.MessageVO
import ru.sudox.design.common.drawables.BadgeDrawable
import ru.sudox.design.common.paint.DrawablePaint
import kotlin.math.max

class MessageItemView : ViewGroup {

    private var maxWidthPercent = 0F
    private var internalPaddingTop = 0
    private var internalPaddingLeft = 0
    private var internalPaddingRight = 0
    private var internalPaddingBottom = 0
    private var marginBetweenTimeAndLikes = 0
    private var marginBetweenContentAndTime = 0
    private var marginBetweenContentAndStatus = 0
    private var marginBetweenTimeAndMessageBorder = 0
    private var marginBetweenTimeAndMessageBottom = 0
    private var currentMessageBackgroundPaint: DrawablePaint? = null
    private var outboundMessageBackgroundPaint: DrawablePaint? = null
    private var inboundMessageBackgroundPaint: DrawablePaint? = null
    private var attachmentsAdapter = MessagesTemplatesAdapter()
    private var timeBadgeDrawable: BadgeDrawable? = null
    private var outboundTextAppearanceId = 0
    private var inboundTextAppearanceId = 0
    private var firstMessageCorner = 0F
    private var otherMessageCorner = 0F
    private var contentBottom = 0
    private var contentRight = 0
    private var contentLeft = 0
    private var contentTop = 0

    private var attachmentsLayout = MediaAttachmentsLayout(context).apply {
        adapter = attachmentsAdapter
        visibility = View.GONE

        this@MessageItemView.addView(this)
    }

    private var timeTextView = AppCompatTextView(context).apply {
        visibility = View.GONE
        this@MessageItemView.addView(this)
    }

    private var textTextView = LinkifiedTextView(context).apply {
        visibility = View.GONE
        this@MessageItemView.addView(this)
    }

    private var statusTextView = AppCompatTextView(context).apply {
        visibility = View.GONE
        this@MessageItemView.addView(this)
    }

    private var likesView = MessageLikesView(context).apply {
        this@MessageItemView.addView(this)
    }

    var vo: MessageVO? = null
        private set

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.messageItemViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.MessageItemView, defStyleAttr, 0).use {
            maxWidthPercent = it.getFloatOrThrow(R.styleable.MessageItemView_maxWidthPercent)
            internalPaddingTop = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_internalPaddingTop)
            internalPaddingLeft = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_internalPaddingLeft)
            internalPaddingRight = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_internalPaddingRight)
            internalPaddingBottom = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_internalPaddingBottom)
            marginBetweenTimeAndLikes = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_marginBetweenTimeAndLikes)
            marginBetweenContentAndTime = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_marginBetweenContentAndTime)
            marginBetweenContentAndStatus = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_marginBetweenContentAndStatus)
            marginBetweenTimeAndMessageBorder = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_marginBetweenTimeAndMessageBorder)
            marginBetweenTimeAndMessageBottom = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_marginBetweenTimeAndMessageBottom)
            firstMessageCorner = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_firstMessageCorner).toFloat()
            otherMessageCorner = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_otherMessageCorner).toFloat()
            outboundMessageBackgroundPaint = DrawablePaint(it.getDrawableOrThrow(R.styleable.MessageItemView_outboundMessageBackground))
            inboundMessageBackgroundPaint = DrawablePaint(it.getDrawableOrThrow(R.styleable.MessageItemView_inboundMessageBackground))
            timeBadgeDrawable = BadgeDrawable(context, false, it.getColorOrThrow(R.styleable.MessageItemView_timeBadgeColor)).apply {
                alpha = (it.getFloatOrThrow(R.styleable.MessageItemView_timeBadgeAlpha) * 255).toInt()
            }

            outboundTextAppearanceId = it.getResourceIdOrThrow(R.styleable.MessageItemView_outboundTextAppearance)
            inboundTextAppearanceId = it.getResourceIdOrThrow(R.styleable.MessageItemView_inboundTextAppearance)
            outboundMessageBackgroundPaint!!.mutateDrawableIfNeed()
            inboundMessageBackgroundPaint!!.mutateDrawableIfNeed()

            setTextAppearance(statusTextView, it.getResourceIdOrThrow(R.styleable.MessageItemView_statusTextAppearance))
        }
    }

    init {
        clipChildren = false
        clipToPadding = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val maxWidthSpec = MeasureSpec.makeMeasureSpec((availableWidth * maxWidthPercent).toInt(), MeasureSpec.AT_MOST)
        var needHeight = 0

        measureChild(timeTextView, maxWidthSpec, heightMeasureSpec)
        measureChild(statusTextView, maxWidthSpec, heightMeasureSpec)
        measureChild(attachmentsLayout, maxWidthSpec, heightMeasureSpec)
        measureChild(textTextView, maxWidthSpec, heightMeasureSpec)
        measureChild(likesView, maxWidthSpec, heightMeasureSpec)

        if (containsOnlyAttachments()) {
            needHeight += attachmentsLayout.measuredHeight
        } else if (textTextView.text.isNotEmpty()) {
            needHeight += textTextView.measuredHeight + internalPaddingTop + internalPaddingBottom
        }

        if (statusTextView.text.isNotEmpty()) {
            needHeight += marginBetweenContentAndStatus + statusTextView.measuredHeight
        }

        if (likesView.visibility == View.VISIBLE) {
            needHeight = max(needHeight, likesView.measuredHeight)
        }

        if (timeTextView.visibility == View.VISIBLE) {
            needHeight = max(needHeight, timeTextView.measuredHeight)
        }

        setMeasuredDimension(availableWidth, needHeight + paddingTop + paddingBottom)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        contentTop = paddingTop

        if (vo?.isSentByMe == true) {
            contentRight = measuredWidth - paddingRight
        } else {
            contentLeft = paddingLeft
        }

        if (containsOnlyAttachments()) {
            contentBottom = contentTop + attachmentsLayout.measuredHeight

            if (vo?.isSentByMe == true) {
                contentLeft = contentRight - attachmentsLayout.measuredWidth
            } else {
                contentRight = contentLeft + attachmentsLayout.measuredWidth
            }

            attachmentsLayout.layout(contentLeft, contentTop, contentRight, contentBottom)
        } else {
            contentTop += internalPaddingTop
            contentBottom = contentTop + textTextView.measuredHeight

            if (vo?.isSentByMe == true) {
                contentRight -= internalPaddingRight
                contentLeft = contentRight - textTextView.measuredWidth
            } else {
                contentLeft += internalPaddingLeft
                contentRight = contentLeft + textTextView.measuredWidth
            }

            currentMessageBackgroundPaint!!.height = contentBottom - contentTop + internalPaddingTop + internalPaddingBottom
            currentMessageBackgroundPaint!!.width = contentRight - contentLeft + internalPaddingLeft + internalPaddingRight
            textTextView.layout(contentLeft, contentTop, contentRight, contentBottom)
        }

        if (likesView.visibility == View.VISIBLE) {
            var left: Int
            var right: Int

            if (vo?.isSentByMe == true) {
                right = contentLeft - marginBetweenTimeAndLikes

                if (!containsOnlyAttachments()) {
                    right -= internalPaddingLeft
                }

                left = right - likesView.measuredWidth
            } else {
                left = contentRight + marginBetweenTimeAndLikes

                if (!containsOnlyAttachments()) {
                    left += internalPaddingRight
                }

                right = left + likesView.measuredWidth
            }

            likesView.layout(left, contentBottom - likesView.measuredHeight, right, contentBottom)
        }

        if (timeTextView.visibility == View.VISIBLE) {
            var left: Int
            var right: Int

            if (vo?.isSentByMe == true) {

            } else {

            }
        }

        if (statusTextView.visibility == View.VISIBLE) {
            var top = contentBottom + marginBetweenContentAndStatus

            if (!containsOnlyAttachments()) {
                top += internalPaddingBottom
            }

            val bottom = top + statusTextView.measuredHeight
            var right: Int
            var left: Int

            if (vo?.isSentByMe == true) {
                right = contentRight

                if (!containsOnlyAttachments()) {
                    right += internalPaddingRight
                }

                left = right - statusTextView.measuredWidth
            } else {
                left = contentLeft

                if (!containsOnlyAttachments()) {
                    left -= internalPaddingLeft
                }

                right = left + statusTextView.measuredWidth
            }

            statusTextView.layout(left, top, right, bottom)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (containsOnlyAttachments()) {
            super.dispatchDraw(canvas)

            val timeY = attachmentsLayout.bottom - marginBetweenTimeAndMessageBottom - timeBadgeDrawable!!.bounds.height()
            val timeX = if (vo?.isSentByMe == true) {
                measuredWidth - paddingRight
            } else {
                paddingLeft + attachmentsLayout.measuredWidth
            } - marginBetweenTimeAndMessageBorder - timeBadgeDrawable!!.bounds.width()

            canvas.withTranslation(x = timeX.toFloat(), y = timeY.toFloat()) {
                timeBadgeDrawable!!.draw(canvas)
            }
        } else {
            canvas.withTranslation(x = (contentLeft - internalPaddingLeft).toFloat(), y = paddingTop.toFloat()) {
                currentMessageBackgroundPaint!!.draw(canvas)
            }

            super.dispatchDraw(canvas)
        }
    }

    /**
     * Устанавливает ViewObject в данную View
     *
     * @param vo ViewObject с данными для отображения
     * @param glide Glide для загрузки аватарок и картинок вложений.
     */
    fun setVO(vo: MessageVO?, glide: GlideRequests) {
        this.vo = vo

        statusTextView.text = vo?.getMessageStatus(context)
        statusTextView.visibility = if (statusTextView.text.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        attachmentsLayout.setVOs(vo?.attachments, glide)
        attachmentsLayout.visibility = if (vo?.attachments != null) {
            View.VISIBLE
        } else {
            View.GONE
        }

        textTextView.text = vo?.text
        textTextView.visibility = if (vo?.text != null) {
            View.VISIBLE
        } else {
            View.GONE
        }

        val time = "20:00" // TODO: Format

        if (containsOnlyAttachments()) {
            timeTextView.text = null
            timeTextView.visibility = View.GONE
            attachmentsAdapter.alignToRight = vo?.isSentByMe == true
            timeBadgeDrawable!!.badgeText = time
        } else {
            val radii = FloatArray(8)
            val appearanceId: Int

            if (vo?.isSentByMe == true) {
                currentMessageBackgroundPaint = outboundMessageBackgroundPaint!!
                appearanceId = outboundTextAppearanceId

                radii[0] = otherMessageCorner
                radii[1] = otherMessageCorner
                radii[2] = firstMessageCorner
                radii[3] = firstMessageCorner
            } else {
                currentMessageBackgroundPaint = inboundMessageBackgroundPaint!!
                appearanceId = inboundTextAppearanceId

                radii[0] = firstMessageCorner
                radii[1] = firstMessageCorner
                radii[2] = otherMessageCorner
                radii[3] = otherMessageCorner
            }

            radii[4] = otherMessageCorner
            radii[5] = otherMessageCorner
            radii[6] = otherMessageCorner
            radii[7] = otherMessageCorner

            setTextAppearance(textTextView, appearanceId)
            timeBadgeDrawable!!.badgeText = null
            timeTextView.visibility = View.VISIBLE
            timeTextView.text = time

            (currentMessageBackgroundPaint!!.drawable as GradientDrawable).cornerRadii = radii
        }

        likesView.setVOs(vo, glide)
        likesView.visibility = if (!vo?.likes.isNullOrEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        requestLayout()
        invalidate()
    }

    private fun containsOnlyAttachments(): Boolean {
        return attachmentsLayout.vos?.isNotEmpty() == true && textTextView.text.isEmpty()
    }
}