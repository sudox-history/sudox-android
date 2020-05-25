package ru.sudox.android.messages.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getFloatOrThrow
import androidx.core.content.res.getFontOrThrow
import androidx.core.content.res.use
import androidx.core.graphics.withTranslation
import ru.sudox.android.media.MediaAttachmentsLayout
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.messages.R
import ru.sudox.android.messages.attachments.MessagesTemplatesAdapter
import ru.sudox.android.messages.vos.MessageVO
import ru.sudox.android.time.dateTimeOf
import ru.sudox.android.time.timestampToTimeString
import ru.sudox.design.common.drawables.BadgeDrawable

class MessageItemView : ViewGroup {

    private var timeBadgeColor = 0
    private var timeBadgeAlpha = 0F
    private var timeBadgeMarginRight = 0
    private var timeBadgeMarginBottom = 0
    private var timeBadgeVerticalPadding = 0
    private var timeBadgeHorizontalPadding = 0
    private var outboundTimeTextColor = 0
    private var inboundTimeTextColor = 0

    private var messageMaxWidth = 0
    private var messagesTemplatesAdapter = MessagesTemplatesAdapter()
    private var timeBadgeDrawable = BadgeDrawable(context, false, 0)
    private var attachmentsLayout = MediaAttachmentsLayout(context).apply {
        adapter = messagesTemplatesAdapter
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

            timeBadgeDrawable.textPaint.let { paint ->
                paint.textSize = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_timeTextSize).toFloat()
                paint.typeface = it.getFontOrThrow(R.styleable.MessageItemView_timeTextFontFamily)
            }

            outboundTimeTextColor = it.getColorOrThrow(R.styleable.MessageItemView_outboundTimeTextColor)
            inboundTimeTextColor = it.getColorOrThrow(R.styleable.MessageItemView_inboundTimeTextColor)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthForSpec = messageMaxWidth

        if (!isContainsAttachments()) {
            // TODO: Remove internal paddings
        }

        val widthSpec = MeasureSpec.makeMeasureSpec(widthForSpec, MeasureSpec.AT_MOST)

        measureChild(attachmentsLayout, widthSpec, heightMeasureSpec)

        var needHeight = paddingBottom + paddingTop

        if (isContainsAttachments()) {
            needHeight += attachmentsLayout.measuredHeight
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (isContainsAttachments()) {
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
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        if (isContainsAttachments()) {
            val timeY = attachmentsLayout.bottom - timeBadgeMarginBottom - timeBadgeDrawable.bounds.height()
            val timeX = if (vo?.isSentByMe == true) {
                measuredWidth - paddingRight
            } else {
                paddingLeft + attachmentsLayout.measuredWidth
            } - timeBadgeMarginRight - timeBadgeDrawable.bounds.width()

            canvas.withTranslation(x = timeX.toFloat(), y = timeY.toFloat()) {
                timeBadgeDrawable.draw(canvas)
            }
        }
    }

    fun setVO(vo: MessageVO?, glide: GlideRequests) {
        this.vo = vo

        attachmentsLayout.setVOs(vo?.attachments, glide)

        if (isContainsAttachments()) {
            attachmentsLayout.visibility = View.VISIBLE
            messagesTemplatesAdapter.alignToRight = vo?.isSentByMe!!

            timeBadgeDrawable.paint.alpha = (timeBadgeAlpha * 255).toInt()
            timeBadgeDrawable.textPaint.color = outboundTimeTextColor
            timeBadgeDrawable.paddingVertical = timeBadgeVerticalPadding
            timeBadgeDrawable.paddingHorizontal = timeBadgeHorizontalPadding
        } else {
            if (vo != null) {
                timeBadgeDrawable.textPaint.color = if (vo.isSentByMe) {
                    outboundTimeTextColor
                } else {
                    inboundTimeTextColor
                }
            }

            timeBadgeDrawable.paint.alpha = 255
            timeBadgeDrawable.paddingVertical = 0
            timeBadgeDrawable.paddingHorizontal = 0
            attachmentsLayout.visibility = View.GONE
        }

        if (vo != null) {
            timeBadgeDrawable.badgeText = timestampToTimeString(context, dateTimeOf(vo.sentTime))
        }

        requestLayout()
        invalidate()
    }

    private fun isContainsAttachments(): Boolean {
        return vo?.attachments?.isNotEmpty() ?: false
    }
}