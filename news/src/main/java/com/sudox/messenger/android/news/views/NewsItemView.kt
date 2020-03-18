package com.sudox.messenger.android.news.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.sudox.messenger.android.news.vos.NewsVO
import com.sudox.messenger.android.people.common.views.HorizontalPeopleItemView

class NewsItemView : ViewGroup {

    var vo: NewsVO? = null
        set(value) {
            peopleItemView.vo = value

            field = value
            requestLayout()
            invalidate()
        }

    private var peopleItemView = HorizontalPeopleItemView(context).apply {
        this@NewsItemView.addView(this)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(peopleItemView, widthMeasureSpec, heightMeasureSpec)

        val needWidth = MeasureSpec.getSize(widthMeasureSpec)
        val needHeight = paddingTop + paddingBottom + peopleItemView.measuredHeight

        setMeasuredDimension(needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val peopleItemViewTop = paddingTop
        val peopleItemViewLeft = paddingLeft
        val peopleItemViewRight = peopleItemViewLeft + peopleItemView.measuredWidth
        val peopleItemViewBottom = peopleItemViewTop + peopleItemView.measuredHeight

        peopleItemView.layout(peopleItemViewLeft, peopleItemViewTop, peopleItemViewRight, peopleItemViewBottom)
    }
}