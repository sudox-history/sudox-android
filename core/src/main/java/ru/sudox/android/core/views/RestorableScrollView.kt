package ru.sudox.android.core.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

const val UNSPECIFIED_SCROLL_PARAM = -1

class RestorableScrollView : NestedScrollView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun scrollTo(x: Int, y: Int) {
        if (childCount > 0) {
            val child = getChildAt(0)
            val lp = child.layoutParams as LayoutParams
            val parentSpaceHorizontal = width - paddingLeft - paddingRight
            val childSizeHorizontal = child.width + lp.leftMargin + lp.rightMargin
            val parentSpaceVertical = height - paddingTop - paddingBottom
            val childSizeVertical = child.height + lp.topMargin + lp.bottomMargin
            val newX = clamp(x, parentSpaceHorizontal, childSizeHorizontal)
            val newY = clamp(y, parentSpaceVertical, childSizeVertical)

            if (newX == scrollX && newY == scrollY) {
                onScrollChanged(newX, newY, UNSPECIFIED_SCROLL_PARAM, UNSPECIFIED_SCROLL_PARAM)
            } else {
                super.scrollTo(newX, newY)
            }
        }
    }

    private fun clamp(n: Int, my: Int, child: Int): Int {
        return if (my >= child || n < 0) {
            0
        } else if (my + n > child) {
            child - my
        } else {
            n
        }
    }
}