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
import androidx.core.content.res.getFontOrThrow
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
import ru.sudox.android.time.dateTimeOf
import ru.sudox.android.time.timestampToTimeString
import ru.sudox.design.common.drawables.BadgeDrawable
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class MessageItemView : ViewGroup {

    private var timeBadgeColor = 0
    private var timeBadgeAlpha = 0F
    private var timeBadgeMarginRight = 0
    private var timeBadgeMarginBottom = 0
    private var timeBadgeVerticalPadding = 0
    private var timeBadgeHorizontalPadding = 0
    private var timeBadgeTimeTextColor = 0
    private var outboundTimeTextColor = 0
    private var inboundTimeTextColor = 0

    private var maxContentWidth = 0
    private var containerLeft = 0
    private var containerRight = 0
    private var containerBottom = 0
    private var containerTop = 0

    private var messageMaxWidth = 0
    private var marginBetweenLikesAndMessage = 0
    private var marginBetweenStatusAndMessage = 0
    private var minMarginBetweenTimeAndContent = 0
    private var messagesTemplatesAdapter = MessagesTemplatesAdapter()
    private var timeBadgeDrawable = BadgeDrawable(context, false, 0)
    private var timeMarginBottom = 0
    private var timeMarginRight = 0

    private var cornerRadius = 0F
    private var leadingCornerRadius = 0F
    private var inboundTextAppearance = 0
    private var outboundTextAppearance = 0
    private var currentBackground: GradientDrawable? = null
    private var outboundBackground: GradientDrawable? = null
    private var inboundBackground: GradientDrawable? = null
    private var messagePaddingBottom = 0
    private var messagePaddingRight = 0
    private var messagePaddingLeft = 0
    private var messagePaddingTop = 0
    private var lastLineWidth = 0

    private var statusTextView = AppCompatTextView(context).apply {
        this@MessageItemView.addView(this)
    }

    private var attachmentsLayout = MediaAttachmentsLayout(context).apply {
        adapter = messagesTemplatesAdapter
        this@MessageItemView.addView(this)
    }

    private var contentTextView = LinkifiedTextView(context).apply {
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
            timeBadgeColor = it.getColorOrThrow(R.styleable.MessageItemView_timeBadgeColor)
            timeBadgeAlpha = it.getFloatOrThrow(R.styleable.MessageItemView_timeBadgeAlpha)
            messageMaxWidth = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_messageMaxWidth)
            timeBadgeMarginRight = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_timeBadgeMarginRight)
            timeBadgeMarginBottom = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_timeBadgeMarginBottom)
            timeBadgeVerticalPadding = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_timeBadgeVerticalPadding)
            timeBadgeHorizontalPadding = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_timeBadgeHorizontalPadding)
            marginBetweenLikesAndMessage = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_marginBetweenLikesAndMessage)
            marginBetweenStatusAndMessage = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_marginBetweenStatusAndMessage)
            minMarginBetweenTimeAndContent = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_minMarginBetweenTimeAndContent)
            timeBadgeTimeTextColor = it.getColorOrThrow(R.styleable.MessageItemView_timeBadgeTimeTextColor)
            timeMarginBottom = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_timeMarginBottom)
            timeMarginRight = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_timeMarginRight)

            timeBadgeDrawable.textPaint.let { paint ->
                paint.textSize = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_timeTextSize).toFloat()
                paint.typeface = it.getFontOrThrow(R.styleable.MessageItemView_timeTextFontFamily)
            }

            cornerRadius = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_cornerRadius).toFloat()
            leadingCornerRadius = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_leadingCornerRadius).toFloat()
            outboundBackground = it.getDrawableOrThrow(R.styleable.MessageItemView_outboundBackground).mutate() as GradientDrawable
            inboundBackground = it.getDrawableOrThrow(R.styleable.MessageItemView_inboundBackground).mutate() as GradientDrawable

            inboundTextAppearance = it.getResourceIdOrThrow(R.styleable.MessageItemView_inboundTextAppearance)
            outboundTextAppearance = it.getResourceIdOrThrow(R.styleable.MessageItemView_outboundTextAppearance)

            messagePaddingTop = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_messagePaddingTop)
            messagePaddingBottom = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_messagePaddingBottom)
            messagePaddingRight = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_messagePaddingRight)
            messagePaddingLeft = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_messagePaddingLeft)

            outboundTimeTextColor = it.getColorOrThrow(R.styleable.MessageItemView_outboundTimeTextColor)
            inboundTimeTextColor = it.getColorOrThrow(R.styleable.MessageItemView_inboundTimeTextColor)

            setTextAppearance(statusTextView, it.getResourceIdOrThrow(R.styleable.MessageItemView_statusTextAppearance))
        }

        clipChildren = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        maxContentWidth = messageMaxWidth

        var containerWidth = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight

        measureChild(likesView, widthMeasureSpec, heightMeasureSpec)
        measureChild(statusTextView, widthMeasureSpec, heightMeasureSpec)

        if (attachmentsLayout.visibility == View.GONE) {
            containerWidth -= messagePaddingRight + messagePaddingLeft
            maxContentWidth -= messagePaddingRight + messagePaddingLeft
        }

        if (likesView.visibility == View.VISIBLE) {
            containerWidth -= likesView.getWidthWhenFull() + marginBetweenLikesAndMessage
        }

        containerWidth = min(maxContentWidth, containerWidth)

        var needHeight = paddingBottom + paddingTop
        val containerWidthSpec = MeasureSpec.makeMeasureSpec(containerWidth, MeasureSpec.AT_MOST)

        if (attachmentsLayout.visibility == View.VISIBLE) {
            measureChild(attachmentsLayout, containerWidthSpec, heightMeasureSpec)
            needHeight += attachmentsLayout.measuredHeight
        } else {
            measureChild(contentTextView, containerWidthSpec, heightMeasureSpec)
            needHeight += contentTextView.measuredHeight + messagePaddingTop + messagePaddingBottom

            if (contentTextView.lineCount > 0) {
                lastLineWidth = ceil(contentTextView.layout.getLineWidth(contentTextView.lineCount - 1)).toInt()

                if (isTimeShowingInAnotherLine()) {
                    needHeight += getTimeHeight() - messagePaddingBottom
                }
            }
        }

        if (statusTextView.visibility == View.VISIBLE) {
            needHeight += statusTextView.measuredHeight + marginBetweenStatusAndMessage
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (attachmentsLayout.visibility == View.VISIBLE) {
            val topBorder = paddingTop
            val bottomBorder = topBorder + attachmentsLayout.measuredHeight

            if (vo?.isSentByMe == true) {
                val rightBorder = measuredWidth - paddingRight
                val leftBorder = rightBorder - attachmentsLayout.measuredWidth

                attachmentsLayout.layout(leftBorder, topBorder, rightBorder, bottomBorder)
            } else {
                val leftBorder = paddingLeft
                val rightBorder = leftBorder + attachmentsLayout.measuredWidth

                attachmentsLayout.layout(leftBorder, topBorder, rightBorder, bottomBorder)
            }

            containerTop = attachmentsLayout.top
            containerLeft = attachmentsLayout.left
            containerRight = attachmentsLayout.right
            containerBottom = attachmentsLayout.bottom
        } else {
            val topBorder = paddingTop + messagePaddingTop
            val bottomBorder = topBorder + contentTextView.measuredHeight

            if (vo?.isSentByMe == true) {
                var rightBorder = measuredWidth - paddingRight - messagePaddingRight
                var leftBorder = rightBorder - contentTextView.measuredWidth

                if (!isTimeShowingInAnotherLine()) {
                    rightBorder -= getTimeWidthModifier()
                    leftBorder -= getTimeWidthModifier()
                }

                contentTextView.layout(leftBorder, topBorder, rightBorder, bottomBorder)
            } else {
                val leftBorder = paddingLeft + messagePaddingLeft
                val rightBorder = leftBorder + contentTextView.measuredWidth

                contentTextView.layout(leftBorder, topBorder, rightBorder, bottomBorder)
            }

            containerTop = contentTextView.top - messagePaddingTop
            containerLeft = contentTextView.left - messagePaddingLeft
            containerRight = contentTextView.right + messagePaddingRight
            containerBottom = contentTextView.bottom + messagePaddingBottom

            if (isTimeShowingInAnotherLine()) {
                containerBottom += getTimeHeight() - messagePaddingBottom
            } else {
                containerRight += getTimeWidthModifier()
            }

            currentBackground?.setBounds(0, 0, containerRight - containerLeft, containerBottom - containerTop)
        }

        if (likesView.visibility == View.VISIBLE) {
            val likesBottom = containerBottom - timeBadgeMarginBottom
            val likesTop = likesBottom - likesView.measuredHeight

            if (vo?.isSentByMe == true) {
                val likesRight = containerLeft - marginBetweenLikesAndMessage
                val likesLeft = likesRight - likesView.measuredWidth

                likesView.layout(likesLeft, likesTop, likesRight, likesBottom)
            } else {
                val likesLeft = containerRight + marginBetweenLikesAndMessage
                val likesRight = likesLeft + likesView.measuredWidth

                likesView.layout(likesLeft, likesTop, likesRight, likesBottom)
            }
        }

        if (statusTextView.visibility == View.VISIBLE) {
            val statusTop = containerBottom + marginBetweenStatusAndMessage
            val statusBottom = statusTop + statusTextView.measuredHeight

            if (vo?.isSentByMe == true) {
                val statusRight = containerRight
                val statusLeft = statusRight - statusTextView.measuredWidth

                statusTextView.layout(statusLeft, statusTop, statusRight, statusBottom)
            } else {
                val statusLeft = containerLeft
                val statusRight = statusLeft + statusTextView.measuredWidth

                statusTextView.layout(statusLeft, statusTop, statusRight, statusBottom)
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (attachmentsLayout.visibility == View.GONE && currentBackground != null) {
            canvas.withTranslation(x = containerLeft.toFloat(), y = containerTop.toFloat()) {
                currentBackground!!.draw(canvas)
            }
        }

        super.dispatchDraw(canvas)

        val timeX: Int
        val timeY: Int

        if (attachmentsLayout.visibility == View.VISIBLE) {
            timeY = attachmentsLayout.bottom - timeBadgeMarginBottom - timeBadgeDrawable.bounds.height()
            timeX = containerRight - timeBadgeMarginRight - timeBadgeDrawable.bounds.width()
        } else {
            timeX = containerRight - timeMarginRight - timeBadgeDrawable.bounds.width()
            timeY = containerBottom - timeBadgeDrawable.bounds.height() - timeMarginBottom
        }

        canvas.withTranslation(x = timeX.toFloat(), y = timeY.toFloat()) {
            timeBadgeDrawable.draw(canvas)
        }
    }

    fun setVO(vo: MessageVO?, glide: GlideRequests) {
        this.vo = vo

        attachmentsLayout.setVOs(vo?.attachments, glide)
        likesView.setVOs(vo, glide)

        if (vo != null) {
            timeBadgeDrawable.badgeText = timestampToTimeString(context, dateTimeOf(vo.sentTime))
        }

        val status = vo?.getMessageStatus(context)

        if (status != null) {
            statusTextView.text = status
            statusTextView.visibility = View.VISIBLE
        } else {
            statusTextView.text = null
            statusTextView.visibility = View.GONE
        }

        if (vo?.attachments?.isNotEmpty() == true) {
            contentTextView.text = null
            contentTextView.visibility = View.GONE
            attachmentsLayout.visibility = View.VISIBLE
            messagesTemplatesAdapter.alignToRight = vo.isSentByMe
            timeBadgeDrawable.isBackgroundEnabled = true

            timeBadgeDrawable.paint.alpha = (timeBadgeAlpha * 255).toInt()
            timeBadgeDrawable.textPaint.color = timeBadgeTimeTextColor
            timeBadgeDrawable.paddingVertical = timeBadgeVerticalPadding
            timeBadgeDrawable.paddingHorizontal = timeBadgeHorizontalPadding
        } else {
            if (vo != null) {
                contentTextView.visibility = View.VISIBLE
                contentTextView.text = vo.text

                val radii = FloatArray(8)

                if (vo.isSentByMe) {
                    timeBadgeDrawable.textPaint.color = outboundTimeTextColor
                    contentTextView.setTextAppearance(outboundTextAppearance)
                    currentBackground = outboundBackground

                    radii[0] = cornerRadius
                    radii[1] = cornerRadius
                    radii[4] = cornerRadius
                    radii[5] = cornerRadius
                    radii[6] = cornerRadius
                    radii[7] = cornerRadius

                    if (vo.isFirstMessage) {
                        radii[2] = leadingCornerRadius
                        radii[3] = leadingCornerRadius
                    } else {
                        radii[2] = cornerRadius
                        radii[3] = cornerRadius
                    }
                } else {
                    timeBadgeDrawable.textPaint.color = inboundTimeTextColor
                    contentTextView.setTextAppearance(inboundTextAppearance)
                    currentBackground = inboundBackground

                    radii[2] = cornerRadius
                    radii[3] = cornerRadius
                    radii[4] = cornerRadius
                    radii[5] = cornerRadius
                    radii[6] = cornerRadius
                    radii[7] = cornerRadius

                    if (vo.isFirstMessage) {
                        radii[0] = leadingCornerRadius
                        radii[1] = leadingCornerRadius
                    } else {
                        radii[0] = cornerRadius
                        radii[1] = cornerRadius
                    }
                }

                currentBackground!!.cornerRadii = radii
            } else {
                contentTextView.text = null
            }

            timeBadgeDrawable.paint.alpha = 255
            timeBadgeDrawable.paddingVertical = 0
            timeBadgeDrawable.paddingHorizontal = 0
            timeBadgeDrawable.isBackgroundEnabled = false
            attachmentsLayout.visibility = View.GONE
        }

        requestLayout()
        invalidate()
    }

    private fun isTimeShowingInAnotherLine(): Boolean {
        var potentialForAdding = messageMaxWidth - lastLineWidth - timeMarginRight

        potentialForAdding -= if (vo?.isSentByMe == true) {
            messagePaddingLeft
        } else {
            messagePaddingRight
        }

        return potentialForAdding < getSpaceForTime()
    }

    private fun getTimeHeight(): Int {
        return timeMarginBottom + timeMarginBottom + timeBadgeDrawable.bounds.height()
    }

    private fun getTimeWidthModifier(): Int {
        val freeWidth = contentTextView.measuredWidth + messagePaddingRight - lastLineWidth - timeMarginRight

        return max(getSpaceForTime() - freeWidth, 0)
    }

    private fun getSpaceForTime(): Int {
        return minMarginBetweenTimeAndContent + timeBadgeDrawable.bounds.width()
    }
}