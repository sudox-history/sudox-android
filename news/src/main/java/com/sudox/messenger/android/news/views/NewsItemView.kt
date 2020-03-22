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
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setCompoundDrawableTintList
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.messenger.android.media.MediaAttachmentsLayout
import com.sudox.messenger.android.media.texts.LinkifiedTextView
import com.sudox.messenger.android.news.R
import com.sudox.messenger.android.news.vos.IS_ACTION_DISABLED
import com.sudox.messenger.android.news.vos.NewsVO
import com.sudox.messenger.android.people.common.views.HorizontalPeopleItemView

class NewsItemView : ViewGroup {

    var vo: NewsVO? = null
        set(value) {
            peopleItemView.vo = value
            contentTextView.text = value?.contentText
            attachmentsLayout.vos = value?.attachments

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

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.newsItemViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.NewsItemView, defStyleAttr, 0).use {
            setTextAppearance(contentTextView, it.getResourceIdOrThrow(R.styleable.NewsItemView_contentTextAppearance))

            marginBetweenPeopleAndContent = it.getDimensionPixelSize(R.styleable.NewsItemView_marginBetweenPeopleAndContent, 0)
            marginBetweenContentAndActions = it.getDimensionPixelSize(R.styleable.NewsItemView_marginBetweenContentAndActions, 0)
            dislikeActionActiveColor = it.getColorOrThrow(R.styleable.NewsItemView_dislikeActionActiveColor)
            likeActionActiveColor = it.getColorOrThrow(R.styleable.NewsItemView_likeActionActiveColor)
            defaultActionColor = it.getColorOrThrow(R.styleable.NewsItemView_defaultActionColor)

            val buttonsStyleId = it.getResourceIdOrThrow(R.styleable.NewsItemView_actionButtonStyle)

            configureButton(likeButton, it.getDrawableOrThrow(R.styleable.NewsItemView_likeIcon), buttonsStyleId)
            configureButton(dislikeButton, it.getDrawableOrThrow(R.styleable.NewsItemView_dislikeIcon), buttonsStyleId)
            configureButton(commentButton, it.getDrawableOrThrow(R.styleable.NewsItemView_commentIcon), buttonsStyleId)
            configureButton(shareButton, it.getDrawableOrThrow(R.styleable.NewsItemView_shareIcon), buttonsStyleId)
        }
    }

    private fun configureButton(view: AppCompatTextView, iconDrawable: Drawable, styleId: Int) = view.let {
        it.setTextColor(defaultActionColor)
        it.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null)
        setCompoundDrawableTintList(it, ColorStateList.valueOf(defaultActionColor))
        setTextAppearance(view, styleId)
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

        val buttonWidthSpec = MeasureSpec.makeMeasureSpec((availableWidth - paddingLeft - paddingRight) / 4, MeasureSpec.EXACTLY)

        likeButton.measure(buttonWidthSpec, heightMeasureSpec)
        dislikeButton.measure(buttonWidthSpec, heightMeasureSpec)
        commentButton.measure(buttonWidthSpec, heightMeasureSpec)
        shareButton.measure(buttonWidthSpec, heightMeasureSpec)

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
        val contentTextBottom = contentTextTop + contentTextView.measuredHeight

        if (!contentTextView.text.isNullOrEmpty()) {
            contentTextView.layout(leftBorder, contentTextTop, rightBorder, contentTextBottom)
        } else {
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

        if (vo!!.likesCount != IS_ACTION_DISABLED) {
            likeButton.layout(leftBorder, buttonsTop, leftBorder + likeButton.measuredWidth, buttonsBottom)
            leftBorder += likeButton.measuredWidth
        }

        if (vo!!.commentsCount != IS_ACTION_DISABLED) {
            commentButton.layout(leftBorder, buttonsTop, leftBorder + commentButton.measuredWidth, buttonsBottom)
            leftBorder += commentButton.measuredWidth
        }

        if (vo!!.sharesCount != IS_ACTION_DISABLED) {
            shareButton.layout(leftBorder, buttonsTop, leftBorder + shareButton.measuredWidth, buttonsBottom)
            leftBorder += shareButton.measuredWidth
        }

        if (vo!!.dislikesCount != IS_ACTION_DISABLED) {
            dislikeButton.layout(leftBorder, buttonsTop, leftBorder + dislikeButton.measuredWidth, buttonsBottom)
        }
    }
}