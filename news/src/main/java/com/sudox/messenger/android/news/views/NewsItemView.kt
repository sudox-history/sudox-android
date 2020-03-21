package com.sudox.messenger.android.news.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Layout
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.messenger.android.news.R
import com.sudox.messenger.android.news.vos.NewsVO
import com.sudox.messenger.android.people.common.views.HorizontalPeopleItemView
import com.sudox.messenger.android.media.texts.LinkifiedTextView

class NewsItemView : ViewGroup {

    var vo: NewsVO? = null
        set(value) {
            peopleItemView.vo = value
            contentTextView.text = value?.contentText

            field = value
            requestLayout()
            invalidate()
        }

    var marginBetweenPeopleAndContentText = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
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
            marginBetweenPeopleAndContentText = it.getDimensionPixelSize(R.styleable.NewsItemView_marginBetweenPeopleAndContentText, 0)

            setTextAppearance(contentTextView, it.getResourceIdOrThrow(R.styleable.NewsItemView_contentTextAppearance))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val childWidthSpec = MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY)

        measureChild(peopleItemView, childWidthSpec, heightMeasureSpec)
        measureChild(contentTextView, childWidthSpec, heightMeasureSpec)

        var needHeight = paddingTop + peopleItemView.measuredHeight + paddingBottom

        if (!contentTextView.text.isNullOrEmpty()) {
            needHeight += contentTextView.measuredHeight + marginBetweenPeopleAndContentText
        }

        setMeasuredDimension(availableWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val leftBorder = paddingLeft
        val peopleItemViewTop = paddingTop
        val rightBorder = leftBorder + peopleItemView.measuredWidth // Все элементы одинаковы по ширине
        val peopleItemViewBottom = peopleItemViewTop + peopleItemView.measuredHeight

        peopleItemView.layout(leftBorder, peopleItemViewTop, rightBorder, peopleItemViewBottom)

        val contentTextTop = peopleItemViewBottom + marginBetweenPeopleAndContentText
        val contentTextBottom = contentTextTop + contentTextView.measuredHeight

        if (!contentTextView.text.isNullOrEmpty()) {
            contentTextView.layout(leftBorder, contentTextTop, rightBorder, contentTextBottom)
        } else {
            contentTextView.layout(0, 0, 0, 0)
        }
    }
}