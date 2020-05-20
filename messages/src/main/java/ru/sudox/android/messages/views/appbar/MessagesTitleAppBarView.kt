package ru.sudox.android.messages.views.appbar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Layout
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
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
                subtitleTextView.setTextColor(if (value.isSubtitleActive()) {
                    activeSubtitleTextColor
                } else {
                    inactiveSubtitleTextColor
                })
            }

            requestLayout()
            invalidate()
        }

    private var titleTextView = AppCompatTextView(context).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
            hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        }

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        ellipsize = TextUtils.TruncateAt.END
        isSingleLine = true
        maxLines = 1

        this@MessagesTitleAppBarView.addView(this)
    }

    private var subtitleTextView = AppCompatTextView(context).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
            hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        }

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        ellipsize = TextUtils.TruncateAt.END
        isSingleLine = true
        maxLines = 1

        this@MessagesTitleAppBarView.addView(this)
    }

    private var marginBetweenTitleAndSubtitle = 0
    private var inactiveSubtitleTextColor = 0
    private var activeSubtitleTextColor = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.messagesTitleAppBarViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.MessagesTitleAppBarView, defStyleAttr, 0).use {
            marginBetweenTitleAndSubtitle = it.getDimensionPixelSizeOrThrow(R.styleable.MessagesTitleAppBarView_marginBetweenTitleAndSubtitle)
            inactiveSubtitleTextColor = it.getColorOrThrow(R.styleable.MessagesTitleAppBarView_inactiveSubtitleTextColor)
            activeSubtitleTextColor = it.getColorOrThrow(R.styleable.MessagesTitleAppBarView_activeSubtitleTextColor)

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