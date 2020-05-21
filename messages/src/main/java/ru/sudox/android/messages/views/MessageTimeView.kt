package ru.sudox.android.messages.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import ru.sudox.android.messages.R

class MessageTimeView : ViewGroup {

    var timeText: String?
        get() = textView.text.toString()
        set(value) {
            textView.text = value
        }

    private val textView = AppCompatTextView(context).apply {
        background = this@MessageTimeView.background

        setPadding(
                this@MessageTimeView.paddingLeft,
                this@MessageTimeView.paddingTop,
                this@MessageTimeView.paddingRight,
                this@MessageTimeView.paddingBottom
        )

        this@MessageTimeView.setPadding(0, 0, 0, 0)
        this@MessageTimeView.background = null
        this@MessageTimeView.addView(this)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.messageTimeViewStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

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