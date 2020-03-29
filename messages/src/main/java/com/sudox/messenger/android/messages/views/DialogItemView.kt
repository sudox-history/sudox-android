package com.sudox.messenger.android.messages.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.*
import androidx.core.text.color
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.design.circleImageView.CircleImageView
import com.sudox.messenger.android.messages.R
import com.sudox.messenger.android.messages.vos.DialogItemViewVO
import kotlin.math.abs
import kotlin.math.max

class DialogItemView : ViewGroup {

    private val nameView = AppCompatTextView(context).apply { addView(this) }
    private val contentTextView = AppCompatTextView(context).apply { addView(this) }
    private val photoView = CircleImageView(context).apply { addView(this) }
    private val dateView = AppCompatTextView(context).apply { addView(this) }
    private val countMessagesView = AppCompatTextView(context).apply { addView(this) }
    private val iconDoneView = AppCompatImageView(context).apply { addView(this) }
    private val iconMutedView = AppCompatImageView(context).apply { addView(this) }

    private var contentText = ""
        set(value) {
            field = value
            contentViewSettingsUpdate()
        }

    private var messageSentByUserHintColor = 0
    private var messageStatusSize = 0
    private var messageStatusIcon: Drawable? = null
    private var messageStatusDoneIcon: Drawable? = null
    private var messageStatusColor = 0

    private var dialogMutedIcon: Drawable? = null
    private var dialogMutedColor = 0
    private var dialogMutedIconSize = 0

    private var imageHeight = 0
    private var imageWidth = 0
    private var imageActiveColor = 0
    private var imageActiveRadius = 0
    private var imageActiveInnerRadius = 0

    private var innerImageToTextMargin = 0
    private var innerDialogNameToTopMargin = 0
    private var innerDialogNameToContentMargin = 0
    private var innerDateToTopMargin = 0
    private var innerMessageStatusMargin = 0
    private var innerContentToRightViewMargin = 0
    private var innerContentToRightBorderMargin = 0
    private var innerMutedIconToDateMargin = 0
    private var innerMutedIconToTopMargin = 0

    private var lastMessageUserName: String? = null
        set(value) {
            field = value
            hintSentByUserViewSettingsUpdate()
        }
    private var isNewMessage = false
        set(value) {
            field = value
            contentViewSettingsUpdate()
        }
    private var isSentByUserMessage = false
        set(value) {
            field = value
            hintSentByUserViewSettingsUpdate()
        }
    private var isStatusDelivered = false
        set(value) {
            field = value
            statusIconViewSettingsUpdate()
        }
    private var isStatusViewed = false
        set(value) {
            field = value
            statusIconViewSettingsUpdate()
        }
    private var isMuted = false
        set(value) {
            field = value

            contentViewSettingsUpdate()
            mutedViewSettingsUpdate()
        }

    private var messageCounter = 0
        set(value) {
            field = value
            counterViewSettingsUpdate()
        }

    private val countMessagesPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paintCountColor = 0
    private var countMessagesRect = RectF()
    private var countMessagesHeight = 0
    private var countMessagesRadius = 0
    private var innerCounterHorizontalMargin = 0
    private var innerCounterVerticalMargin = 0

    private var dialogContentTextAppearance = 0
    private var dialogContentNewTextAppearance = 0

    var vo: DialogItemViewVO? = null
        set(value) {
            field = value

            contentText = value!!.previewMessage
            isNewMessage = !value.isViewed
            messageCounter = value.messagesCount

            photoView.setImageDrawable(value.dialogPhoto)
            nameView.text = value.dialogName
            isSentByUserMessage = value.isLastMessageByMe

            isStatusDelivered = value.isSentMessageDelivered
            isStatusViewed = value.isSentMessageViewed

            dateView.text = value.dateView
            isMuted = value.isMuted

            lastMessageUserName = value.lastMessageUsername

            invalidate()
            requestLayout()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.dialogItemViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.DialogItemView, defStyleAttr, 0).use {
            setTextAppearance(nameView, it.getResourceIdOrThrow(R.styleable.DialogItemView_dialogNameTextAppearance))
            setTextAppearance(dateView, it.getResourceIdOrThrow(R.styleable.DialogItemView_dialogDateTextAppearance))
            dialogContentTextAppearance = it.getResourceIdOrThrow(R.styleable.DialogItemView_dialogContentTextAppearance)
            dialogContentNewTextAppearance = it.getResourceIdOrThrow(R.styleable.DialogItemView_dialogNewContentTextAppearance)
            setTextAppearance(countMessagesView, it.getResourceIdOrThrow(R.styleable.DialogItemView_dialogMessageCountTextAppearance))

            imageHeight = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageHeight)
            imageWidth = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageWidth)

            imageActiveColor = it.getColorOrThrow(R.styleable.DialogItemView_imageActiveColor)
            imageActiveRadius = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageActiveRadius)
            imageActiveInnerRadius = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageActiveInnerRadius)

            messageSentByUserHintColor = it.getColorOrThrow(R.styleable.DialogItemView_messageSentByUserHintColor)
            messageStatusSize = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_messageStatusSize)
            messageStatusIcon = it.getDrawableOrThrow(R.styleable.DialogItemView_messageStatusIcon)
            messageStatusDoneIcon = it.getDrawableOrThrow(R.styleable.DialogItemView_messageStatusDoneIcon)
            messageStatusColor = it.getColorOrThrow(R.styleable.DialogItemView_messageStatusColor)

            dialogMutedIcon = it.getDrawableOrThrow(R.styleable.DialogItemView_dialogMutedIcon)
            dialogMutedColor = it.getColorOrThrow(R.styleable.DialogItemView_dialogMutedColor)
            dialogMutedIconSize = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_dialogMutedIconSize)

            innerImageToTextMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerImageToTextMargin)
            innerDialogNameToTopMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerDialogNameToTopMargin)
            innerDialogNameToContentMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerDialogNameToContentMargin)
            innerDateToTopMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerDateToTopMargin)
            innerMessageStatusMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerMessageStatusMargin)
            innerContentToRightViewMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerContentToRightViewMargin)
            innerContentToRightBorderMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerContentToRightBorderMargin)
            innerCounterHorizontalMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_countMessagesInnerHorizontalMargin)
            innerCounterVerticalMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_countMessagesInnerVerticalMargin)
            innerMutedIconToDateMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerMutedIconToDateMargin)
            innerMutedIconToTopMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerMutedIconToTopMargin)

            countMessagesHeight = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_countMessagesHeight)
            countMessagesRadius = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_countMessagesRadius)

            paintCountColor = it.getColorOrThrow(R.styleable.DialogItemView_messageCountColor)
        }

        viewSettingsUpdate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)

        measureChild(dateView, widthMeasureSpec, heightMeasureSpec)
        measureChild(nameView, widthMeasureSpec, heightMeasureSpec)
        measureChild(photoView, widthMeasureSpec, heightMeasureSpec)
        measureChild(iconDoneView, widthMeasureSpec, heightMeasureSpec)
        measureChild(countMessagesView, widthMeasureSpec, heightMeasureSpec)
        measureChild(iconMutedView, widthMeasureSpec, heightMeasureSpec)
        measureChild(contentTextView, widthMeasureSpec, heightMeasureSpec)

        val contentTextWidth: Int
        val tempMaxStatusWidth = max(dateView.measuredWidth + if (isMuted) {
            iconMutedView.measuredWidth
        } else {
            0
        }, if (isNewMessage) {
            countMessagesView.measuredWidth + 2 * innerCounterHorizontalMargin
        } else {
            0
        })

        contentTextWidth = if (availableWidth != 0) {
            if (tempMaxStatusWidth > innerContentToRightBorderMargin) {
                availableWidth - paddingLeft - imageWidth - innerImageToTextMargin -
                        tempMaxStatusWidth - innerContentToRightViewMargin - paddingRight
            } else {
                availableWidth - paddingLeft - imageWidth - innerImageToTextMargin -
                        innerContentToRightBorderMargin - paddingRight
            }
        } else {
            contentTextView.measuredWidth
        }
        measureChild(contentTextView, MeasureSpec.makeMeasureSpec(contentTextWidth, MeasureSpec.EXACTLY), heightMeasureSpec)
        measureChild(nameView, MeasureSpec.makeMeasureSpec(contentTextWidth, MeasureSpec.EXACTLY), heightMeasureSpec)

        val needHeight = paddingTop + max(innerDialogNameToTopMargin +
                nameView.measuredHeight + innerDialogNameToContentMargin +
                contentTextView.measuredHeight, photoView.measuredHeight) + paddingBottom

        val needWidth = paddingLeft + photoView.measuredWidth + innerImageToTextMargin +
                contentTextWidth + max(innerContentToRightBorderMargin, tempMaxStatusWidth -
                innerContentToRightBorderMargin) + paddingRight

        setMeasuredDimension(if (availableWidth != 0) availableWidth else needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l
        val rightBorder = width - paddingRight

        val photoLeftBorder = paddingLeft
        val photoTopBorder = paddingTop
        val photoBottomBorder = photoTopBorder + photoView.measuredHeight
        val photoRightBorder = photoLeftBorder + photoView.measuredWidth

        val dialogNameLeftBorder = photoRightBorder + innerImageToTextMargin
        val dialogNameTopBorder = paddingTop + innerDialogNameToTopMargin
        val dialogNameBottomBorder = dialogNameTopBorder + nameView.measuredHeight
        val dialogNameRightBorder = dialogNameLeftBorder + nameView.measuredWidth

        val contentTopBorder = dialogNameBottomBorder + innerDialogNameToContentMargin
        val contentRightBorder = dialogNameLeftBorder + contentTextView.measuredWidth
        val contentBottomBorder = contentTopBorder + contentTextView.measuredHeight

        val dateLeftBorder = rightBorder - dateView.measuredWidth
        val dateTopBorder = paddingTop + innerDateToTopMargin
        val dateBottomBorder = dateTopBorder + dateView.measuredHeight

        val counterLeftBorder = rightBorder - countMessagesView.measuredWidth - innerCounterHorizontalMargin
        val counterTopBorder = dialogNameBottomBorder + innerDialogNameToContentMargin
        val counterRightBorder = rightBorder - innerCounterHorizontalMargin
        val counterBottomBorder = counterTopBorder + dateView.measuredHeight

        countMessagesView.layout(counterLeftBorder, counterTopBorder, counterRightBorder, counterBottomBorder)

        val doneIconTopBorder = dialogNameBottomBorder + innerDialogNameToContentMargin
        val doneIconBottomBorder = doneIconTopBorder + iconDoneView.measuredHeight
        val doneIconLeftBorder = rightBorder - iconDoneView.measuredWidth

        iconDoneView.layout(doneIconLeftBorder, doneIconTopBorder, rightBorder, doneIconBottomBorder)

        val mutedIconTopBorder = dateTopBorder + (abs(iconMutedView.measuredHeight - dateView.measuredHeight)) / 2
        val mutedIconRightBorder = dateLeftBorder - innerMutedIconToDateMargin
        val mutedIconBottomBorder = mutedIconTopBorder + iconMutedView.measuredHeight
        val mutedIconLeftBorder = mutedIconRightBorder - iconMutedView.measuredWidth

        iconMutedView.layout(mutedIconLeftBorder, mutedIconTopBorder, mutedIconRightBorder, mutedIconBottomBorder)

        photoView.layout(photoLeftBorder, photoTopBorder, photoRightBorder, photoBottomBorder)
        nameView.layout(dialogNameLeftBorder, dialogNameTopBorder, dialogNameRightBorder, dialogNameBottomBorder)
        contentTextView.layout(dialogNameLeftBorder, contentTopBorder, contentRightBorder, contentBottomBorder)
        dateView.layout(dateLeftBorder, dateTopBorder, rightBorder, dateBottomBorder)
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (isNewMessage && messageCounter != 0) {
            val leftCountBorder = width.toFloat() -
                    paddingRight - innerCounterHorizontalMargin * 2 - countMessagesView.measuredWidth
            val topCountBorder = paddingTop.toFloat() + innerDialogNameToTopMargin +
                    nameView.measuredHeight + innerDialogNameToContentMargin - innerCounterVerticalMargin
            val rightCountBorder = width.toFloat() - paddingRight
            val bottomCountBorder = topCountBorder + 2 * innerCounterVerticalMargin +
                    countMessagesView.measuredHeight.toFloat()

            countMessagesRect.set(leftCountBorder, topCountBorder, rightCountBorder, bottomCountBorder)

            if (isMuted) {
                countMessagesPaint.color = dialogMutedColor
            } else {
                countMessagesPaint.color = paintCountColor
            }

            canvas.drawRoundRect(
                    countMessagesRect,
                    countMessagesRadius.toFloat(),
                    countMessagesRadius.toFloat(),
                    countMessagesPaint
            )
        }
        super.dispatchDraw(canvas)
    }

    private fun viewSettingsUpdate() {
        contentViewSettingsUpdate()
        dialogNameViewSettingsUpdate()
        dialogImageSettingsUpdate()
        dateViewSettingsUpdate()
        counterViewSettingsUpdate()
        statusIconViewSettingsUpdate()
        hintSentByUserViewSettingsUpdate()
        mutedViewSettingsUpdate()
    }

    private fun contentViewSettingsUpdate() {
        //text appearance
        contentTextView.text = contentText
        if (isNewMessage and !isMuted) {
            setTextAppearance(contentTextView, dialogContentNewTextAppearance)
        } else {
            setTextAppearance(contentTextView, dialogContentTextAppearance)
        }

        contentTextView.apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            gravity = Gravity.LEFT
            ellipsize = TextUtils.TruncateAt.END
            maxLines = if (isNewMessage && !isMuted) 2 else 1
        }
    }

    private fun dialogNameViewSettingsUpdate() {
        nameView.apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            gravity = Gravity.LEFT
            ellipsize = TextUtils.TruncateAt.END
            isSingleLine = true
            maxLines = 1
        }
    }

    private fun dialogImageSettingsUpdate() {
        photoView.apply {
            layoutParams = LayoutParams(imageWidth, imageHeight)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun dateViewSettingsUpdate() {
        dateView.apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER_VERTICAL
            includeFontPadding = false
            isSingleLine = true
            maxLines = 1
        }
    }

    private fun counterViewSettingsUpdate() {
        countMessagesView.apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER_VERTICAL
            visibility = if (messageCounter == 0 || !isNewMessage) View.GONE else View.VISIBLE

            setPadding(0, 0, 0, 0)

            isSingleLine = true
            maxLines = 1
            includeFontPadding = false
            text = messageCounter.toString()
        }
    }

    private fun statusIconViewSettingsUpdate() {
        if (isStatusDelivered && !isNewMessage) {
            messageStatusDoneIcon?.setTint(messageStatusColor)
            messageStatusIcon?.setTint(messageStatusColor)

            iconDoneView.apply {
                visibility = View.VISIBLE
                layoutParams = LayoutParams(messageStatusSize, messageStatusSize)
                iconDoneView.setImageDrawable(if (isStatusViewed) {
                    messageStatusDoneIcon
                } else {
                    messageStatusIcon
                })
            }
        } else {
            iconDoneView.visibility = View.GONE
        }
    }

    private fun hintSentByUserViewSettingsUpdate() {
        if (isSentByUserMessage || lastMessageUserName != null) {
            contentTextView.text = SpannableStringBuilder()
                    .color(messageSentByUserHintColor) {
                        append(if (isSentByUserMessage) {
                            resources.getString(R.string.message_sent_by_user)
                        } else {
                            lastMessageUserName
                        })
                    }.append(contentText)
        }
    }

    private fun mutedViewSettingsUpdate() {
        dialogMutedIcon?.setTint(dialogMutedColor)

        iconMutedView.apply {
            layoutParams = LayoutParams(dialogMutedIconSize, dialogMutedIconSize)
            visibility = if (isMuted) View.VISIBLE else View.GONE
            adjustViewBounds = true

            setImageDrawable(dialogMutedIcon)
        }
    }
}