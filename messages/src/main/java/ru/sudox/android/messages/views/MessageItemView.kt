package ru.sudox.android.messages.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import ru.sudox.android.media.MediaAttachmentsLayout
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.media.texts.LinkifiedTextView
import ru.sudox.android.messages.templates.MessageTemplatesAdapter
import ru.sudox.android.messages.vos.MessageVO

class MessageItemView : ViewGroup {

    private var containerWidth = 0
    private var containerHeight = 0
    private var maxWidthPercent = 0.75F // TODO: From config
    private var marginBetweenTimeAndLikes = 20 // TODO: From config
    private var marginBetweenContentAndTime = 12 // TODO: From config
    private var marginBetweenContentAndStatus = 26 // TODO: From config
    private var firstMessageCorner = 12F // TODO: From config
    private var otherMessageCorner = 42F // TODO: From config
    private var attachmentsAdapter = MessageTemplatesAdapter()

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

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

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

        if (containsOnlyAttachment()) {
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
        if (containsOnlyAttachment()) {
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

        if (containsOnlyAttachment()) {
            timeTextView.text = null
            timeTextView.visibility = View.GONE

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
            timeTextView.text = "16:00" // TODO: Format
            timeTextView.visibility = View.VISIBLE
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

    private fun containsOnlyAttachment(): Boolean {
        return attachmentsLayout.vos?.size == 1 && textTextView.text.isEmpty()
    }
}