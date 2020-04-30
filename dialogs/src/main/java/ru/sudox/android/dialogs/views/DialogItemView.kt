package ru.sudox.android.dialogs.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.dialogs.R
import ru.sudox.android.dialogs.vos.DialogVO
import ru.sudox.android.media.images.views.avatar.AvatarImageView
import ru.sudox.android.time.formatTime
import kotlin.math.abs
import kotlin.math.max

class DialogItemView : ViewGroup {

    private val nameView = AppCompatTextView(context).apply { addView(this) }
    private val contentTextView = AppCompatTextView(context).apply { addView(this) }
    private val dateView = AppCompatTextView(context).apply { addView(this) }
    private val countMessagesView = AppCompatTextView(context).apply { addView(this) }
    private val iconDoneView = AppCompatImageView(context).apply { addView(this) }
    private val iconMutedView = AppCompatImageView(context).apply { addView(this) }

    private var dialogAvatarView = AvatarImageView(context).apply { addView(this) }
    private var messageSentByUserHintColor = 0
    private var messageStatusSize = 0
    private var messageStatusIcon: Drawable? = null
    private var messageStatusDoneIcon: Drawable? = null
    private var messageStatusColor = 0

    private var dialogMutedIcon: Drawable? = null
    private var dialogMutedColor = 0
    private var dialogMutedIconSize = 0

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

    private val countMessagesPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paintCountColor = 0
    private var countMessagesRect = RectF()
    private var countMessagesHeight = 0
    private var countMessagesRadius = 0
    private var innerCounterHorizontalMargin = 0
    private var innerCounterVerticalMargin = 0

    private var dialogContentTextAppearance = 0
    private var dialogContentNewTextAppearance = 0

    var vo: DialogVO? = null
        private set

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

            val imageHeight = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageHeight)
            val imageWidth = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageWidth)

            dialogAvatarView.layoutParams = LayoutParams(imageWidth, imageHeight)

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
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)

        measureChild(dateView, widthMeasureSpec, heightMeasureSpec)
        measureChild(nameView, widthMeasureSpec, heightMeasureSpec)
        measureChild(dialogAvatarView, widthMeasureSpec, heightMeasureSpec)
        measureChild(iconDoneView, widthMeasureSpec, heightMeasureSpec)
        measureChild(countMessagesView, widthMeasureSpec, heightMeasureSpec)
        measureChild(iconMutedView, widthMeasureSpec, heightMeasureSpec)
        measureChild(contentTextView, widthMeasureSpec, heightMeasureSpec)

        val contentTextWidth: Int
        val countMaxWidth = if (!vo!!.isViewedByMe) countMessagesView.measuredWidth + 2 * innerCounterHorizontalMargin else 0


        val availableContentAndInfoWidth = availableWidth - dialogAvatarView.measuredWidth - innerImageToTextMargin

        contentTextWidth = if (availableWidth != 0) {
            if (countMaxWidth > innerContentToRightBorderMargin) {
                availableContentAndInfoWidth - countMaxWidth - innerContentToRightViewMargin
            } else {
                availableContentAndInfoWidth - innerContentToRightBorderMargin
            }
        } else {
            contentTextView.measuredWidth
        }

        measureChild(contentTextView, MeasureSpec.makeMeasureSpec(contentTextWidth, MeasureSpec.EXACTLY), heightMeasureSpec)
        measureChild(nameView, MeasureSpec.makeMeasureSpec(availableContentAndInfoWidth -
                dateView.measuredWidth - innerMutedIconToDateMargin -
                if(vo!!.isMuted) iconMutedView.measuredWidth + innerMutedIconToDateMargin else 0,
                MeasureSpec.EXACTLY), heightMeasureSpec)

        val needHeight = paddingTop + max(innerDialogNameToTopMargin +
                nameView.measuredHeight + innerDialogNameToContentMargin +
                contentTextView.measuredHeight, dialogAvatarView.measuredHeight) + paddingBottom
        val needWidth = paddingLeft + dialogAvatarView.measuredWidth + innerImageToTextMargin +
                contentTextWidth + max(innerContentToRightBorderMargin, countMaxWidth +
                innerContentToRightViewMargin) + paddingRight

        setMeasuredDimension(if (availableWidth != 0) availableWidth else needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l
        val rightBorder = width - paddingRight

        val photoLeftBorder = paddingLeft
        val photoTopBorder = paddingTop
        val photoBottomBorder = photoTopBorder + dialogAvatarView.measuredHeight
        val photoRightBorder = photoLeftBorder + dialogAvatarView.measuredWidth

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

        dialogAvatarView.layout(photoLeftBorder, photoTopBorder, photoRightBorder, photoBottomBorder)
        nameView.layout(dialogNameLeftBorder, dialogNameTopBorder, dialogNameRightBorder, dialogNameBottomBorder)
        contentTextView.layout(dialogNameLeftBorder, contentTopBorder, contentRightBorder, contentBottomBorder)
        dateView.layout(dateLeftBorder, dateTopBorder, rightBorder, dateBottomBorder)
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (!vo!!.isViewedByMe && vo!!.messagesCount != 0) {
            val leftCountBorder = width.toFloat() -
                    paddingRight - innerCounterHorizontalMargin * 2 - countMessagesView.measuredWidth
            val topCountBorder = paddingTop.toFloat() + innerDialogNameToTopMargin +
                    nameView.measuredHeight + innerDialogNameToContentMargin - innerCounterVerticalMargin
            val rightCountBorder = width.toFloat() - paddingRight
            val bottomCountBorder = topCountBorder + 2 * innerCounterVerticalMargin +
                    countMessagesView.measuredHeight.toFloat()

            countMessagesRect.set(leftCountBorder, topCountBorder, rightCountBorder, bottomCountBorder)
            if (vo!!.isMuted) {
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
        dateViewSettingsUpdate()
        counterViewSettingsUpdate()
        statusIconViewSettingsUpdate()
        mutedViewSettingsUpdate()
    }

    private fun contentViewSettingsUpdate() {
        contentTextView.apply {
            text = vo!!.getLastMessage(context)
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            ellipsize = TextUtils.TruncateAt.END
            maxLines = if (!vo!!.isViewedByMe && !vo!!.isMuted) 2 else 1

            if (!vo!!.isViewedByMe and !vo!!.isMuted) {
                setTextAppearance(contentTextView, dialogContentNewTextAppearance)
            } else {
                setTextAppearance(contentTextView, dialogContentTextAppearance)
            }
        }
    }

    private fun dialogNameViewSettingsUpdate() {
        nameView.apply {
            text = vo!!.getName()
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            ellipsize = TextUtils.TruncateAt.END
            isSingleLine = true
            maxLines = 1
        }
    }

    private fun dateViewSettingsUpdate() {
        //date view settings
        dateView.apply {
            text = formatTime(context, dateToLowerCase = true, time = vo!!.time)
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER_VERTICAL
            includeFontPadding = false
            isSingleLine = true
            maxLines = 1
        }
    }

    private fun counterViewSettingsUpdate() {
        countMessagesView.apply {
            setPadding(0, 0, 0, 0)

            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER_VERTICAL
            visibility = if (vo!!.messagesCount == 0 || vo!!.isViewedByMe) View.GONE else View.VISIBLE
            isSingleLine = true
            maxLines = 1
            includeFontPadding = false
            text = vo!!.messagesCount.toString()
        }
    }

    private fun statusIconViewSettingsUpdate() {
        if (vo!!.isSentMessageDelivered && vo!!.isViewedByMe) {
            messageStatusDoneIcon?.setTint(messageStatusColor)
            messageStatusIcon?.setTint(messageStatusColor)

            iconDoneView.apply {
                visibility = View.VISIBLE
                layoutParams = LayoutParams(messageStatusSize, messageStatusSize)

                setImageDrawable(if (vo!!.isSentMessageViewed) {
                    messageStatusDoneIcon
                } else {
                    messageStatusIcon
                })
            }
        } else {
            iconDoneView.visibility = View.GONE
        }
    }

    private fun mutedViewSettingsUpdate() {
        iconMutedView.apply {
            layoutParams = LayoutParams(dialogMutedIconSize, dialogMutedIconSize)
            visibility = if (vo!!.isMuted) View.VISIBLE else View.GONE
            adjustViewBounds = true

            setImageDrawable(dialogMutedIcon?.apply {
                setTint(dialogMutedColor)
            })
        }
    }

    /**
     * Устанавливает ViewObject в данную View
     *
     * @param vo ViewObject, который нужно использовать
     * @param glide Glide для загрузки аватарки
     */
    fun setVO(vo: DialogVO, glide: GlideRequests) {
        this.vo = vo

        dialogAvatarView.setVO(vo, glide)
        viewSettingsUpdate()
        requestLayout()
        invalidate()
    }
}