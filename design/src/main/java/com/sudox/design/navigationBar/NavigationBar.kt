package com.sudox.design.navigationBar

import android.content.Context
import android.os.Parcelable
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

            val buttonWidth = (availableWidth - paddingLeft - paddingRight) / buttons.size
            val buttonWidthSpec = MeasureSpec.makeMeasureSpec(buttonWidth, MeasureSpec.EXACTLY)

            buttons.forEach {
                it.measure(buttonWidthSpec, heightMeasureSpec)
            }

            setMeasuredDimension(availableWidth, minimumHeight)
        } else {
            setMeasuredDimension(0, 0)
        }
    }

    override fun onClick(view: View) {
        buttons.forEach { it.setClicked(it == view) }
        listener?.onButtonClicked(view.tag as Int)
    }

    fun setSelectedItem(tag: Int, callback: Boolean = true) {
        buttons.forEach { it.setClicked(it.tag == tag) }

        if (callback) {
            listener?.onButtonClicked(tag)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (buttons.isEmpty()) {
            return
        }

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

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val state = parcelable as NavigationBarState

        state.apply {
            super.onRestoreInstanceState(superState)
            readToView(this@NavigationBar)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        return NavigationBarState(superState!!).apply {
            writeFromView(this@NavigationBar)
        }
    }

    fun addItem(itemId: Int, @StringRes titleId: Int, @DrawableRes iconId: Int) {
        buttons.add(createItem().apply {
            set(titleId, iconId)
            tag = itemId
        })
    }

    internal fun createItem() = NavigationBarButton(context).apply {
        setOnClickListener(this@NavigationBar)

        id = View.generateViewId()
        layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
        )

        addView(this)
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