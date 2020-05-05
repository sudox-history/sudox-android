package ru.sudox.android.messages.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

class MessageItemView : ViewGroup {

    var messageLikesView = MessageLikesView(context).apply {
        this@MessageItemView.addView(this)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(messageLikesView, widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        messageLikesView.layout(measuredWidth - messageLikesView.measuredWidth, 0, measuredWidth, messageLikesView.measuredHeight)
    }
}