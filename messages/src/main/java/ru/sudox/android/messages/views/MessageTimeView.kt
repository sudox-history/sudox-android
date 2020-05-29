package ru.sudox.android.messages.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import ru.sudox.android.messages.R

/**
 * View для отображения даты.
 * Используется для липкой View при скролле, а также в списке.
 */
class MessageTimeView : ViewGroup {

    var timeText: String?
        get() = textView.text.toString()
        set(value) {
            textView.text = value
        }

    private val textView = AppCompatTextView(context).apply {
        this@MessageTimeView.addView(this)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.messageTimeViewStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.MessageTimeView, defStyleAttr, 0).use {
            textView.background = it.getDrawableOrThrow(R.styleable.MessageTimeView_timeBackground)

            val topPadding = it.getDimensionPixelSize(R.styleable.MessageTimeView_timeTopPadding, 0)
            val bottomPadding = it.getDimensionPixelSize(R.styleable.MessageTimeView_timeBottomPadding, 0)
            val rightPadding = it.getDimensionPixelSize(R.styleable.MessageTimeView_timeRightPadding, 0)
            val leftPadding = it.getDimensionPixelSize(R.styleable.MessageTimeView_timeLeftPadding, 0)

            textView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding)
            setTextAppearance(textView, it.getResourceIdOrThrow(R.styleable.MessageTimeView_timeTextAppearance))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(textView, widthMeasureSpec, heightMeasureSpec)

        val needHeight = paddingTop + textView.measuredHeight + paddingBottom
        val needWidth = MeasureSpec.getSize(widthMeasureSpec)

        setMeasuredDimension(needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val left = measuredWidth / 2 - textView.measuredWidth / 2
        val right = left + textView.measuredWidth

        textView.layout(left, paddingTop, right, paddingTop + textView.measuredHeight)
    }
}