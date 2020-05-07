package ru.sudox.android.messages.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getFloatOrThrow
import androidx.core.content.res.use
import androidx.core.graphics.withTranslation
import ru.sudox.android.media.MediaAttachmentsLayout
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.media.texts.LinkifiedTextView
import ru.sudox.android.messages.R
import ru.sudox.android.messages.templates.MessageTemplatesAdapter
import ru.sudox.android.messages.vos.MessageVO
import ru.sudox.design.common.drawables.BadgeDrawable

class MessageItemView : ViewGroup {

    private var containerWidth = 0
    private var containerHeight = 0
    private var maxWidthPercent = 0F
    private var marginBetweenTimeAndLikes = 0
    private var marginBetweenContentAndTime = 0
    private var marginBetweenContentAndStatus = 0
    private var marginBetweenTimeAndAttachmentEnd = 0
    private var marginBetweenTimeAndAttachmentBottom = 0
    private var firstMessageCorner = 0F
    private var otherMessageCorner = 0F
    private var attachmentsAdapter = MessageTemplatesAdapter()
    private var timeBadgeDrawable: BadgeDrawable? = null

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
            marginBetweenTimeAndLikes = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_marginBetweenTimeAndLikes)
            marginBetweenContentAndTime = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_marginBetweenContentAndTime)
            marginBetweenContentAndStatus = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_marginBetweenContentAndStatus)
            marginBetweenTimeAndAttachmentEnd = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_marginBetweenTimeAndAttachmentEnd)
            marginBetweenTimeAndAttachmentBottom = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_marginBetweenTimeAndAttachmentBottom)
            firstMessageCorner = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_firstMessageCorner).toFloat()
            otherMessageCorner = it.getDimensionPixelSizeOrThrow(R.styleable.MessageItemView_otherMessageCorner).toFloat()
            timeBadgeDrawable = BadgeDrawable(context, false, it.getColorOrThrow(R.styleable.MessageItemView_timeBadgeColor)).apply {
                alpha = (it.getFloatOrThrow(R.styleable.MessageItemView_timeBadgeAlpha) * 255).toInt()
            }
        }
    }

    init {
        clipChildren = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val maxWidthSpec = MeasureSpec.makeMeasureSpec((availableWidth * maxWidthPercent).toInt(), MeasureSpec.AT_MOST)

        measureChild(timeTextView, maxWidthSpec, heightMeasureSpec)
        measureChild(statusTextView, maxWidthSpec, heightMeasureSpec)
        measureChild(attachmentsLayout, maxWidthSpec, heightMeasureSpec)

        var needHeight = paddingTop + paddingBottom

        if (containsOnlyAttachments()) {
            needHeight += attachmentsLayout.measuredHeight
            containerHeight = 0
            containerWidth = 0
        }

        if (statusTextView.text.isNotEmpty()) {
            needHeight += marginBetweenContentAndStatus + statusTextView.measuredWidth
        }

        setMeasuredDimension(availableWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (containsOnlyAttachments()) {
            val top = paddingTop
            val bottom = top + attachmentsLayout.measuredHeight
            val right: Int
            val left: Int

            if (vo?.sentByMe == true) {
                right = measuredWidth - paddingRight
                left = right - attachmentsLayout.measuredWidth
            } else {
                left = paddingLeft
                right = left + attachmentsLayout.measuredWidth
            }

            attachmentsLayout.layout(left, top, right, bottom)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        if (containsOnlyAttachments()) {
            val timeX = measuredWidth - marginBetweenTimeAndAttachmentEnd - paddingRight - timeBadgeDrawable!!.bounds.width()
            val timeY = measuredHeight - marginBetweenTimeAndAttachmentBottom - paddingBottom - timeBadgeDrawable!!.bounds.height()

            canvas.withTranslation(x = timeX.toFloat(), y = timeY.toFloat()) {
                timeBadgeDrawable!!.draw(canvas)
            }
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

        val time = "20:00" // TODO: Format

        if (containsOnlyAttachments()) {
            timeTextView.text = null
            timeTextView.visibility = View.GONE
            timeBadgeDrawable!!.badgeText = time

            if (vo?.sentByMe == true) {
                attachmentsAdapter.topLeftCropRadius = otherMessageCorner
                attachmentsAdapter.topRightCropRadius = firstMessageCorner
                attachmentsAdapter.bottomRightCropRadius = otherMessageCorner
                attachmentsAdapter.bottomLeftCropRadius = otherMessageCorner
            } else {
                attachmentsAdapter.topLeftCropRadius = firstMessageCorner
                attachmentsAdapter.topRightCropRadius = otherMessageCorner
                attachmentsAdapter.bottomRightCropRadius = otherMessageCorner
                attachmentsAdapter.bottomLeftCropRadius = otherMessageCorner
            }
        } else {
            timeTextView.text = time
            timeTextView.visibility = View.VISIBLE
            timeBadgeDrawable!!.badgeText = null
        }

        textTextView.text = vo?.text
        textTextView.visibility = if (vo?.text != null) {
            View.VISIBLE
        } else {
            View.GONE
        }

        likesView.setVOs(vo?.likes, glide)
        likesView.visibility = if (vo?.likes != null) {
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