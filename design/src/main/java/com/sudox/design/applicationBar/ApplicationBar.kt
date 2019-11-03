package com.sudox.design.applicationBar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat
import com.sudox.design.R
import com.sudox.design.applicationBar.applicationBarButton.ApplicationBarButton
import com.sudox.design.isLayoutRtl
import kotlin.math.max
import kotlin.math.min

private const val BUTTONS_IN_END_COUNT = 3

const val APPBAR_START_BUTTON_TAG = 0
const val APPBAR_FIRST_END_BUTTON_TAG = 1
const val APPBAR_SECOND_END_BUTTON_TAG = 2
const val APPBAR_THIRD_END_BUTTON_TAG = 3

class ApplicationBar : ViewGroup, View.OnClickListener {

    var listener: ApplicationBarListener? = null
    var buttonAtStart: ApplicationBarButton? = null
    var buttonsAtEnd = arrayOfNulls<ApplicationBarButton>(BUTTONS_IN_END_COUNT)

    internal var titleTextRes: Int = 0
    internal var titleTextView = AppCompatTextView(context)
    internal var contentView: View? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.applicationBarStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.ApplicationBar, defStyleAttr, 0).use {
            TextViewCompat.setTextAppearance(titleTextView, it.getResourceIdOrThrow(R.styleable.ApplicationBar_titleTextAppearance))
        }

        initButtons()
    }

    private fun initButtons() {
        buttonAtStart = createButton()
        buttonAtStart!!.tag = APPBAR_START_BUTTON_TAG

        for (i in buttonsAtEnd.indices) {
            buttonsAtEnd[i] = createButton()
            buttonsAtEnd[i]!!.tag = when (i) {
                0 -> APPBAR_FIRST_END_BUTTON_TAG
                1 -> APPBAR_SECOND_END_BUTTON_TAG
                2 -> APPBAR_THIRD_END_BUTTON_TAG
                else -> null
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        measureChild(buttonAtStart, widthMeasureSpec, heightMeasureSpec)

        if (contentView != null) {
            measureChild(contentView!!, widthMeasureSpec, heightMeasureSpec)
        }

        val buttonsAtEndWidth = buttonsAtEnd.sumBy {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
            it!!.measuredWidth
        }

        val needWidth = paddingStart +
                buttonAtStart!!.measuredWidth +
                (contentView?.measuredWidth ?: 0) +
                buttonsAtEndWidth +
                paddingEnd

        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            availableWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            min(needWidth, availableWidth)
        } else {
            needWidth
        }

        val needHeight = paddingTop +
                max(max(contentView?.measuredHeight ?: 0, buttonAtStart!!.measuredHeight), buttonsAtEnd[0]!!.measuredHeight) +
                paddingBottom

        val measuredHeight = if (heightMode == MeasureSpec.EXACTLY) {
            availableHeight
        } else if (heightMode == MeasureSpec.AT_MOST) {
            min(needHeight, availableHeight)
        } else {
            needHeight
        }

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val isRtl = isLayoutRtl()

        layoutStart(left, right, top, bottom, isRtl)

        if (buttonsAtEnd.isNotEmpty()) {
            layoutEnd(left, right, top, bottom, isRtl)
        }
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val state = parcelable as ApplicationBarState

        state.apply {
            super.onRestoreInstanceState(superState)
            readToView(this@ApplicationBar)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        return ApplicationBarState(superState!!).apply {
            writeFromView(this@ApplicationBar)
        }
    }

    override fun onClick(view: View) {
        listener?.onButtonClicked(view.tag as Int)
    }

    internal fun layoutStart(left: Int, right: Int, top: Int, bottom: Int, rtl: Boolean) {
        var leftBorder = getStartLeftBorder(left, right, rtl)
        var rightBorder = leftBorder

        if (buttonAtStart!!.visibility == View.VISIBLE) {
            if (rtl) {
                leftBorder -= buttonAtStart!!.measuredWidth
            } else {
                rightBorder += buttonAtStart!!.measuredWidth
            }

            buttonAtStart!!.layout(leftBorder, top, rightBorder, bottom)
        }

        if (contentView?.visibility == View.VISIBLE) {
            val contentViewLeftBorder = width / 2 - contentView!!.measuredWidth / 2
            val contentViewRightBorder = contentViewLeftBorder + contentView!!.measuredWidth
            val contentViewTopBorder = height / 2 - contentView!!.measuredHeight / 2
            val contentViewBottomBorder = contentViewTopBorder + contentView!!.measuredHeight

            contentView!!.layout(
                    contentViewLeftBorder,
                    contentViewTopBorder,
                    contentViewRightBorder,
                    contentViewBottomBorder
            )
        }
    }

    internal fun layoutEnd(left: Int, right: Int, top: Int, bottom: Int, rtl: Boolean) {
        var leftBorder = getEndLeftBorder(left, right, rtl)
        var rightBorder = leftBorder

        for (i in buttonsAtEnd.size - 1 downTo 0) {
            val button = buttonsAtEnd[i]

            if (button!!.visibility != View.VISIBLE) {
                continue
            }

            if (rtl) {
                rightBorder += button.measuredWidth
            } else {
                leftBorder -= button.measuredWidth
            }

            button.layout(leftBorder, top, rightBorder, bottom)

            if (rtl) {
                leftBorder = rightBorder
            } else {
                rightBorder = leftBorder
            }
        }
    }

    internal fun getStartLeftBorder(left: Int, right: Int, rtl: Boolean): Int {
        var leftBorder: Int

        if (rtl) {
            leftBorder = right - paddingStart

            if (buttonAtStart!!.visibility == View.VISIBLE) {
                leftBorder += buttonAtStart!!.paddingRight
            }
        } else {
            leftBorder = left + paddingStart

            if (buttonAtStart!!.visibility == View.VISIBLE) {
                leftBorder -= buttonAtStart!!.paddingLeft
            }
        }

        return leftBorder
    }

    internal fun getEndLeftBorder(left: Int, right: Int, rtl: Boolean): Int {
        return if (rtl) {
            left + paddingEnd - buttonAtStart!!.paddingRight
        } else {
            right - paddingEnd + buttonAtStart!!.paddingLeft
        }
    }

    private fun createButton(): ApplicationBarButton {
        return ApplicationBarButton(context).apply {
            id = View.generateViewId()
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            setOnClickListener(this@ApplicationBar)
            addView(this)
        }
    }

    fun setContentView(view: View?) {
        if (contentView != null && contentView != view) {
            removeViewInLayout(contentView)
        }

        contentView = view

        if (view != null) {
            addView(view)
        } else {
            requestLayout()
            invalidate()
        }
    }

    fun setTitleText(text: String?, fromRes: Boolean = false) {
        titleTextView.text = text

        if (!fromRes) {
            titleTextRes = 0
        }

        if (text != null) {
            setContentView(titleTextView)
        } else {
            setContentView(null)
        }
    }

    fun setTitleText(@StringRes textRes: Int) {
        val text = resources.getString(textRes)
        titleTextRes = textRes
        setTitleText(text, true)
    }

    fun resetButtonsEnd() {
        for (button in buttonsAtEnd) {
            button!!.reset()
        }
    }

    fun reset() {
        buttonAtStart!!.reset()
        setTitleText(null)
        setContentView(null)
        resetButtonsEnd()
    }
}