package ru.sudox.android.messages.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import ru.sudox.android.messages.R
import ru.sudox.android.messages.vos.MessagesTitleAppBarVO

class MessagesTitleAppBarView : ViewGroup {

    var vo: MessagesTitleAppBarVO? = null
        set(value) {
            field = value?.apply {
                titleTextView.text = getTitle(context)
                subtitleTextView.text = getSubtitle(context)
            }

            requestLayout()
            invalidate()
        }

    private var titleTextView = AppCompatTextView(context).apply { this@MessagesTitleAppBarView.addView(this) }
    private var subtitleTextView = AppCompatTextView(context).apply { this@MessagesTitleAppBarView.addView(this) }
    private var marginBetweenTitleAndSubtitle = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.messagesTitleAppBarViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.MessagesTitleAppBarView, defStyleAttr, 0).use {
            marginBetweenTitleAndSubtitle = it.getDimensionPixelSizeOrThrow(R.styleable.MessagesTitleAppBarView_marginBetweenTitleAndSubtitle)

            setTextAppearance(titleTextView, it.getResourceIdOrThrow(R.styleable.MessagesTitleAppBarView_titleTextAppearance))
            setTextAppearance(subtitleTextView, it.getResourceIdOrThrow(R.styleable.MessagesTitleAppBarView_subtitleTextAppearance))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(titleTextView, widthMeasureSpec, heightMeasureSpec)
        measureChild(subtitleTextView, widthMeasureSpec, heightMeasureSpec)

        val needWidth = MeasureSpec.getSize(widthMeasureSpec)
        val needHeight = paddingTop + paddingBottom + titleTextView.measuredHeight + subtitleTextView.measuredHeight

        setMeasuredDimension(needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val leftBorder = paddingLeft
        val topBorder = paddingTop

        val rightTitleBorder = leftBorder + titleTextView.measuredWidth
        val bottomTitleBorder = topBorder + titleTextView.measuredHeight

        titleTextView.layout(leftBorder, topBorder, rightTitleBorder, bottomTitleBorder)

        val rightSubtitleBorder = leftBorder + subtitleTextView.measuredWidth
        val topSubtitleBorder = bottomTitleBorder + marginBetweenTitleAndSubtitle
        val bottomSubtitleBorder = topSubtitleBorder + subtitleTextView.measuredHeight

        subtitleTextView.layout(leftBorder, topSubtitleBorder, rightSubtitleBorder, bottomSubtitleBorder)
    }
}