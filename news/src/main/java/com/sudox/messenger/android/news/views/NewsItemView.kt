package com.sudox.messenger.android.news.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Layout
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setCompoundDrawableTintList
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.messenger.android.media.MediaAttachmentsLayout
import com.sudox.messenger.android.media.texts.LinkifiedTextView
import com.sudox.messenger.android.media.texts.helpers.formatNumber
import com.sudox.messenger.android.news.R
import com.sudox.messenger.android.news.vos.IS_ACTION_DISABLED
import com.sudox.messenger.android.news.vos.NewsVO
import com.sudox.messenger.android.people.common.views.HorizontalPeopleItemView
import kotlin.math.roundToInt

class NewsItemView : ViewGroup {

    var vo: NewsVO? = null
        set(value) {
            peopleItemView.vo = value
            contentTextView.text = value?.contentText
            attachmentsLayout.vos = value?.attachments

            if (value != null) {
                dislikeButton.text = formatNumber(context, value.dislikesCount)
                commentButton.text = formatNumber(context, value.commentsCount)
                shareButton.text = formatNumber(context, value.sharesCount)

                bindButton(likeButton, likeDrawable!!, likeActiveDrawable!!, likeActionActiveColor, value.isLikeSet, value.likesCount)
                bindButton(dislikeButton, dislikeDrawable!!, dislikeActiveDrawable!!, dislikeActionActiveColor, value.isDislikeSet, value.dislikesCount)
            }

            field = value
            requestLayout()
            invalidate()
        }

    var marginBetweenPeopleAndContent = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var marginBetweenContentAndActions = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var defaultActionColor = 0
        set(value) {
            field = value
            vo = vo
        }

    var dislikeActionActiveColor = 0
        set(value) {
            field = value
            vo = vo
        }

    var likeActionActiveColor = 0
        set(value) {
            field = value
            vo = vo
        }

    private var likeButton = AppCompatTextView(context).apply {
        gravity = Gravity.CENTER_VERTICAL
        isClickable = true
        isFocusable = true

        this@NewsItemView.addView(this)
    }

    private var dislikeButton = AppCompatTextView(context).apply {
        gravity = Gravity.CENTER_VERTICAL
        isClickable = true
        isFocusable = true

        this@NewsItemView.addView(this)
    }

    private var shareButton = AppCompatTextView(context).apply {
        gravity = Gravity.CENTER_VERTICAL
        isClickable = true
        isFocusable = true

        this@NewsItemView.addView(this)
    }

    private var commentButton = AppCompatTextView(context).apply {
        gravity = Gravity.CENTER_VERTICAL
        isClickable = true
        isFocusable = true

        this@NewsItemView.addView(this)
    }

    private var attachmentsLayout = MediaAttachmentsLayout(context).apply {
        layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        )

        this@NewsItemView.addView(this)
    }

    private var peopleItemView = HorizontalPeopleItemView(context).apply {
        this@NewsItemView.addView(this)
    }

    private var contentTextView = LinkifiedTextView(context).apply {
        setTextIsSelectable(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
            hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        }

        this@NewsItemView.addView(this)
    }

    private var likeDrawable: Drawable? = null
    private var dislikeDrawable: Drawable? = null
    private var likeActiveDrawable: Drawable? = null
    private var dislikeActiveDrawable: Drawable? = null
    private var actionBarPaddingLeft = 0
    private var actionBarPaddingRight = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.newsItemViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.NewsItemView, defStyleAttr, 0).use {
            setTextAppearance(contentTextView, it.getResourceIdOrThrow(R.styleable.NewsItemView_contentTextAppearance))

            actionBarPaddingLeft = it.getDimensionPixelSize(R.styleable.NewsItemView_actionBarPaddingLeft, 0)
            actionBarPaddingRight = it.getDimensionPixelSize(R.styleable.NewsItemView_actionBarPaddingRight, 0)
            marginBetweenPeopleAndContent = it.getDimensionPixelSize(R.styleable.NewsItemView_marginBetweenPeopleAndContent, 0)
            marginBetweenContentAndActions = it.getDimensionPixelSize(R.styleable.NewsItemView_marginBetweenContentAndActions, 0)
            dislikeActionActiveColor = it.getColorOrThrow(R.styleable.NewsItemView_dislikeActionActiveColor)
            likeActionActiveColor = it.getColorOrThrow(R.styleable.NewsItemView_likeActionActiveColor)
            defaultActionColor = it.getColorOrThrow(R.styleable.NewsItemView_defaultActionColor)

            likeDrawable = it.getDrawableOrThrow(R.styleable.NewsItemView_likeIcon)
            dislikeDrawable = it.getDrawableOrThrow(R.styleable.NewsItemView_dislikeIcon)
            likeActiveDrawable = it.getDrawableOrThrow(R.styleable.NewsItemView_likeActiveIcon)
            dislikeActiveDrawable = it.getDrawableOrThrow(R.styleable.NewsItemView_dislikeActiveIcon)

            val buttonsStyleId = it.getResourceIdOrThrow(R.styleable.NewsItemView_actionButtonStyle)
            val marginBetweenButtonIconAndText = it.getDimensionPixelSizeOrThrow(R.styleable.NewsItemView_marginBetweenButtonIconAndText)

            configureButton(likeButton, likeDrawable!!, buttonsStyleId, marginBetweenButtonIconAndText)
            configureButton(dislikeButton, dislikeDrawable!!, buttonsStyleId, marginBetweenButtonIconAndText)
            configureButton(commentButton, it.getDrawableOrThrow(R.styleable.NewsItemView_commentIcon), buttonsStyleId, marginBetweenButtonIconAndText)
            configureButton(shareButton, it.getDrawableOrThrow(R.styleable.NewsItemView_shareIcon), buttonsStyleId, marginBetweenButtonIconAndText)
        }
    }

    private fun configureButton(view: AppCompatTextView, iconDrawable: Drawable, styleId: Int, marginBetweenButtonIconAndText: Int) = view.let {
        it.compoundDrawablePadding = marginBetweenButtonIconAndText
        it.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null)

        setTextAppearance(it, styleId)
        setCompoundDrawableTintList(it, ColorStateList.valueOf(defaultActionColor))
        it.setTextColor(defaultActionColor)
    }

    private fun bindButton(view: AppCompatTextView,
                           defaultDrawable: Drawable,
                           activeDrawable: Drawable,
                           activeColor: Int,
                           isActive: Boolean,
                           count: Int
    ) = view.let {
        it.text = formatNumber(context, count)

        val color: Int
        val drawable: Drawable

        if (isActive) {
            drawable = activeDrawable
            color = activeColor
        } else {
            drawable = defaultDrawable
            color = defaultActionColor
        }

        it.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        setCompoundDrawableTintList(it, ColorStateList.valueOf(color))
        it.setTextColor(color)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val childWidthSpec = MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY)

        measureChild(peopleItemView, childWidthSpec, heightMeasureSpec)
        measureChild(contentTextView, childWidthSpec, heightMeasureSpec)
        measureChild(attachmentsLayout, childWidthSpec, heightMeasureSpec)

        var needHeight = paddingTop + peopleItemView.measuredHeight + paddingBottom

        if (!contentTextView.text.isNullOrEmpty()) {
            needHeight += contentTextView.measuredHeight + marginBetweenPeopleAndContent
        }

        if (attachmentsLayout.childCount > 0) {
            needHeight += attachmentsLayout.measuredHeight + marginBetweenPeopleAndContent
        }

        measureChild(likeButton, widthMeasureSpec, heightMeasureSpec)
        measureChild(dislikeButton, widthMeasureSpec, heightMeasureSpec)
        measureChild(commentButton, widthMeasureSpec, heightMeasureSpec)
        measureChild(shareButton, widthMeasureSpec, heightMeasureSpec)

        needHeight += likeButton.measuredHeight + marginBetweenContentAndActions // Все кнопки одинаковы по высоте

        setMeasuredDimension(availableWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var leftBorder = paddingLeft
        val peopleItemViewTop = paddingTop
        val rightBorder = leftBorder + peopleItemView.measuredWidth // Все элементы одинаковы по ширине
        val peopleItemViewBottom = peopleItemViewTop + peopleItemView.measuredHeight

        peopleItemView.layout(leftBorder, peopleItemViewTop, rightBorder, peopleItemViewBottom)

        val contentTextTop = peopleItemViewBottom + marginBetweenPeopleAndContent
        var contentTextBottom = contentTextTop + contentTextView.measuredHeight

        if (!contentTextView.text.isNullOrEmpty()) {
            contentTextView.layout(leftBorder, contentTextTop, rightBorder, contentTextBottom)
        } else {
            contentTextBottom = peopleItemViewBottom
            contentTextView.layout(0, 0, 0, 0)
        }

        val attachmentsTop = contentTextBottom + marginBetweenPeopleAndContent
        val attachmentsBottom = attachmentsTop + attachmentsLayout.measuredHeight
        val buttonsTop = if (attachmentsLayout.childCount > 0) {
            attachmentsLayout.layout(leftBorder, attachmentsTop, rightBorder, attachmentsBottom)
            attachmentsBottom
        } else {
            attachmentsLayout.layout(0, 0, 0, 0)
            contentTextBottom
        } + marginBetweenContentAndActions

        val buttonsBottom = buttonsTop + likeButton.measuredHeight
        var activeButtonsWidth = 0
        var activeButtonsCount = 0

        if (vo!!.likesCount != IS_ACTION_DISABLED) {
            activeButtonsWidth += likeButton.measuredWidth
            activeButtonsCount++
        }

        if (vo!!.commentsCount != IS_ACTION_DISABLED) {
            activeButtonsWidth += commentButton.measuredWidth
            activeButtonsCount++
        }

        if (vo!!.sharesCount != IS_ACTION_DISABLED) {
            activeButtonsWidth += shareButton.measuredWidth
            activeButtonsCount++
        }

        if (vo!!.dislikesCount != IS_ACTION_DISABLED) {
            activeButtonsWidth += dislikeButton.measuredWidth
            activeButtonsCount++
        }

        val marginBetweenButtons = ((measuredWidth - activeButtonsWidth - paddingLeft - paddingRight -
                actionBarPaddingLeft - actionBarPaddingRight) / (activeButtonsCount - 1).toFloat()).roundToInt()

        leftBorder += actionBarPaddingLeft

        if (vo!!.likesCount != IS_ACTION_DISABLED) {
            likeButton.layout(leftBorder, buttonsTop, leftBorder + likeButton.measuredWidth, buttonsBottom)
            leftBorder += likeButton.measuredWidth + marginBetweenButtons
        }

        if (vo!!.commentsCount != IS_ACTION_DISABLED) {
            commentButton.layout(leftBorder, buttonsTop, leftBorder + commentButton.measuredWidth, buttonsBottom)
            leftBorder += commentButton.measuredWidth + marginBetweenButtons
        }

        if (vo!!.sharesCount != IS_ACTION_DISABLED) {
            shareButton.layout(leftBorder, buttonsTop, leftBorder + shareButton.measuredWidth, buttonsBottom)
            leftBorder += shareButton.measuredWidth + marginBetweenButtons
        }

        if (vo!!.dislikesCount != IS_ACTION_DISABLED) {
            dislikeButton.layout(leftBorder, buttonsTop, leftBorder + dislikeButton.measuredWidth, buttonsBottom)
        }
    }
}