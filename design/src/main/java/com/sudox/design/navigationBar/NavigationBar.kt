package com.sudox.design.navigationBar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sudox.design.R
import com.sudox.design.navigationBar.navigationBarButton.NavigationBarButton
import java.util.LinkedList

class NavigationBar : ViewGroup, View.OnClickListener {

    var buttons = LinkedList<NavigationBarButton>()
    var listener: NavigationBarListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.navigationBarStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (buttons.isNotEmpty()) {
            val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
            val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

            val buttonWidth = (availableWidth - paddingLeft - paddingRight) / buttons.size
            val buttonWidthSpec = MeasureSpec.makeMeasureSpec(buttonWidth, MeasureSpec.EXACTLY)

            buttons.forEach {
                it.measure(buttonWidthSpec, heightMeasureSpec)
            }

            setMeasuredDimension(availableWidth, availableHeight)
        } else {
            setMeasuredDimension(0, 0)
        }
    }

    override fun onClick(view: View) {
        buttons.forEach {
            it.setClicked(it == view)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val topBorder = paddingTop
        val bottomBorder = topBorder + buttons.first().measuredHeight
        var leftBorder = paddingLeft
        var rightBorder: Int

        buttons.forEach {
            rightBorder = leftBorder + it.measuredWidth
            it.layout(leftBorder, topBorder, rightBorder, bottomBorder)
            leftBorder = rightBorder
        }
    }

    fun addItem(itemId: Int, @StringRes titleId: Int, @DrawableRes iconId: Int) {
        buttons.add(NavigationBarButton(context).apply {
            setOnClickListener(this@NavigationBar)
            set(titleId, iconId)

            tag = itemId
            layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT
            )

            addView(this)
        })
    }

    fun removeItem(index: Int) {
        if (index == 0) {
            buttons.removeFirst()
        } else if (index == buttons.lastIndex) {
            buttons.removeLast()
        } else {
            buttons.removeAt(index)
        }
    }
}