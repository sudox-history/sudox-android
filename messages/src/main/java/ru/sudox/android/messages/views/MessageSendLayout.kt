package ru.sudox.android.messages.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getIntegerOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.getStringOrThrow
import androidx.core.content.res.use
import androidx.core.graphics.withTranslation
import androidx.core.widget.TextViewCompat.setTextAppearance
import ru.sudox.android.messages.R

class MessageSendLayout : ViewGroup {

    private var strokeWidth = 0
    private var cornerRadius = 0F
    private var cornerRadiusWhenOneLine = 0F
    private var sendButtonMarginBottom = 0
    private var contentBlockPaddingLeft = 0
    private var contentBlockPaddingRight = 0
    private var marginBetweenTextAndAttachmentButton = 0
    private var marginBetweenContentBlockAndSendButton = 0
    private var contentBlockBackground: GradientDrawable? = null
    private var attachmentImageButton = AppCompatImageButton(context).apply {
        this@MessageSendLayout.addView(this)
    }

    private val sendImageButton = AppCompatImageButton(context).apply {
        this@MessageSendLayout.addView(this)
    }

    private val editText = AppCompatEditText(context, null, 0).apply {
        isFocusable = true
        isFocusableInTouchMode = true
        isClickable = true

        this@MessageSendLayout.addView(this)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.messageSendLayoutStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.MessageSendLayout, defStyleAttr, 0).use {
            sendButtonMarginBottom = it.getDimensionPixelSizeOrThrow(R.styleable.MessageSendLayout_sendButtonMarginBottom)
            marginBetweenTextAndAttachmentButton = it.getDimensionPixelSizeOrThrow(R.styleable.MessageSendLayout_marginBetweenTextAndAttachmentButton)
            marginBetweenContentBlockAndSendButton = it.getDimensionPixelSizeOrThrow(R.styleable.MessageSendLayout_marginBetweenContentBlockAndSendButton)
            contentBlockPaddingRight = it.getDimensionPixelSizeOrThrow(R.styleable.MessageSendLayout_contentBlockPaddingRight)
            contentBlockPaddingLeft = it.getDimensionPixelSizeOrThrow(R.styleable.MessageSendLayout_contentBlockPaddingLeft)
            contentBlockBackground = it.getDrawableOrThrow(R.styleable.MessageSendLayout_contentBlockBackground) as GradientDrawable
            cornerRadiusWhenOneLine = it.getDimensionPixelSizeOrThrow(R.styleable.MessageSendLayout_cornerRadiusWhenOneLine).toFloat()
            cornerRadius = it.getDimensionPixelSizeOrThrow(R.styleable.MessageSendLayout_cornerRadius).toFloat()
            strokeWidth = it.getDimensionPixelSizeOrThrow(R.styleable.MessageSendLayout_strokeWidth)

            editText.maxLines = it.getIntegerOrThrow(R.styleable.MessageSendLayout_textMaxLines)
            editText.hint = it.getStringOrThrow(R.styleable.MessageSendLayout_textHint)
            editText.setPadding(0,
                    it.getDimensionPixelSize(R.styleable.MessageSendLayout_textPaddingTop, 0), 0,
                    it.getDimensionPixelSize(R.styleable.MessageSendLayout_textPaddingBottom, 0))

            val attachmentButtonSize = it.getDimensionPixelSizeOrThrow(R.styleable.MessageSendLayout_attachmentButtonSize)

            attachmentImageButton.setImageDrawable(it.getDrawableOrThrow(R.styleable.MessageSendLayout_attachmentButtonIcon))
            attachmentImageButton.imageTintList = ColorStateList.valueOf(it.getColorOrThrow(R.styleable.MessageSendLayout_attachmentButtonIconTint))
            attachmentImageButton.layoutParams = LayoutParams(attachmentButtonSize, attachmentButtonSize)

            val sendButtonSize = it.getDimensionPixelSizeOrThrow(R.styleable.MessageSendLayout_sendButtonSize)

            sendImageButton.setImageDrawable(it.getDrawableOrThrow(R.styleable.MessageSendLayout_sendButtonIcon))
            sendImageButton.imageTintList = ColorStateList.valueOf(it.getColorOrThrow(R.styleable.MessageSendLayout_sendButtonIconTint))
            sendImageButton.layoutParams = LayoutParams(sendButtonSize, sendButtonSize)

            setTextAppearance(editText, it.getResourceIdOrThrow(R.styleable.MessageSendLayout_textAppearance))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(sendImageButton, widthMeasureSpec, heightMeasureSpec)
        measureChild(attachmentImageButton, widthMeasureSpec, heightMeasureSpec)

        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val textWidth = availableWidth -
                sendImageButton.measuredWidth -
                attachmentImageButton.measuredWidth -
                marginBetweenContentBlockAndSendButton -
                marginBetweenTextAndAttachmentButton -
                contentBlockPaddingRight -
                contentBlockPaddingLeft -
                paddingLeft -
                paddingRight -
                2 * strokeWidth

        editText.measure(MeasureSpec.makeMeasureSpec(textWidth, MeasureSpec.EXACTLY), heightMeasureSpec)

        val contentBlockBackgroundHeight = editText.measuredHeight + 2 * strokeWidth
        val contentBlockBackgroundWidth = availableWidth -
                paddingLeft -
                paddingRight -
                sendImageButton.measuredWidth -
                marginBetweenContentBlockAndSendButton

        contentBlockBackground!!.cornerRadius = if (editText.lineCount > 1) {
            cornerRadius
        } else {
            cornerRadiusWhenOneLine
        }

        contentBlockBackground!!.setBounds(0, 0, contentBlockBackgroundWidth, contentBlockBackgroundHeight)
        setMeasuredDimension(availableWidth, paddingTop + paddingBottom + contentBlockBackgroundHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val sendButtonRight = measuredWidth - paddingRight
        val sendButtonLeft = sendButtonRight - sendImageButton.measuredWidth
        val sendButtonBottom = measuredHeight - paddingBottom - sendButtonMarginBottom
        val sendButtonTop = sendButtonBottom - sendImageButton.measuredHeight

        sendImageButton.layout(sendButtonLeft, sendButtonTop, sendButtonRight, sendButtonBottom)

        val attachButtonLeft = paddingLeft + strokeWidth + contentBlockPaddingLeft
        val attachButtonRight = attachButtonLeft + attachmentImageButton.measuredWidth
        val attachButtonBottom = measuredHeight - paddingBottom - strokeWidth - editText.paddingBottom
        val attachButtonTop = attachButtonBottom - attachmentImageButton.measuredHeight

        attachmentImageButton.layout(attachButtonLeft, attachButtonTop, attachButtonRight, attachButtonBottom)

        val editTextLeft = attachButtonRight + marginBetweenTextAndAttachmentButton
        val editTextRight = editTextLeft + editText.measuredWidth
        val editTextBottom = measuredHeight - paddingBottom - strokeWidth
        val editTextTop = editTextBottom - editText.measuredHeight

        editText.layout(editTextLeft, editTextTop, editTextRight, editTextBottom)
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.withTranslation(paddingLeft.toFloat(), paddingTop.toFloat()) {
            contentBlockBackground!!.draw(canvas)
        }

        super.dispatchDraw(canvas)
    }
}