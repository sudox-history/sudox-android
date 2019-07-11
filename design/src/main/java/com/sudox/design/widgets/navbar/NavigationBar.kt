package com.sudox.design.widgets.navbar

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import com.sudox.design.R
import com.sudox.design.helpers.isLayoutRtl
import com.sudox.design.widgets.navbar.button.NavigationBarButton
import com.sudox.design.widgets.navbar.button.NavigationBarButtonParams
import com.sudox.design.widgets.navbar.title.NavigationBarTitleParams

private const val BUTTONS_IN_END_COUNT = 3

const val NAVBAR_START_BUTTON_TAG = 0
const val NAVBAR_FIRST_END_BUTTON_TAG = 1
const val NAVBAR_SECOND_END_BUTTON_TAG = 2
const val NAVBAR_THIRD_END_BUTTON_TAG = 3

class NavigationBar : ViewGroup, View.OnClickListener {

    internal var buttonParams = NavigationBarButtonParams()
    internal var titleParams = NavigationBarTitleParams()

    var buttonsClickCallback: ((Int) -> (Unit))? = null
    var buttonStart: NavigationBarButton? = null
    var buttonsEnd = arrayOfNulls<NavigationBarButton>(BUTTONS_IN_END_COUNT)

    internal var titleTextRes: Int = 0
    internal var titleTextView = TextView(context)
    internal var contentView: View? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.navigationBarStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        with(context.obtainStyledAttributes(attrs, R.styleable.NavigationBar, defStyleAttr, R.style.NavigationBar)) {
            buttonParams.readFromAttrs(this, context.theme)
            titleParams.readFromAttrs(this, context.theme)
            recycle()
        }

        initButtons()
        initTitle()
    }

    private fun initButtons() {
        buttonStart = createButton()
        buttonStart!!.tag = NAVBAR_START_BUTTON_TAG

        for (i in 0 until buttonsEnd.size) {
            buttonsEnd[i] = createButton()
            buttonsEnd[i]!!.tag = when (i) {
                0 -> NAVBAR_FIRST_END_BUTTON_TAG
                1 -> NAVBAR_SECOND_END_BUTTON_TAG
                2 -> NAVBAR_THIRD_END_BUTTON_TAG
                else -> null
            }
        }
    }

    private fun initTitle() {
        titleTextView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        titleTextView.gravity = Gravity.CENTER_VERTICAL
        titleTextView.typeface = titleParams.textTypeface
        titleTextView.setTextColor(titleParams.textColor)
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleParams.textSize)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(buttonStart, widthMeasureSpec, heightMeasureSpec)

        if (contentView != null) {
            measureChild(contentView!!, widthMeasureSpec, heightMeasureSpec)
        }

        for (button in buttonsEnd) {
            measureChild(button, widthMeasureSpec, heightMeasureSpec)
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val isRtl = isLayoutRtl()

        layoutStart(left, right, top, bottom, isRtl)

        if (buttonsEnd.isNotEmpty()) {
            layoutEnd(left, right, top, bottom, isRtl)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val state = NavigationBarSavedState(superState!!)
        state.readFromView(this)
        return state
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val state = parcelable as NavigationBarSavedState
        super.onRestoreInstanceState(state.superState)
        state.writeToView(this)
    }

    override fun onClick(view: View) {
        buttonsClickCallback?.invoke(view.tag as Int)
    }

    internal fun layoutStart(left: Int, right: Int, top: Int, bottom: Int, rtl: Boolean) {
        var leftBorder = getStartLeftBorder(left, right, rtl)
        var rightBorder = leftBorder

        if (buttonStart!!.visibility == View.VISIBLE) {
            if (rtl) {
                leftBorder -= buttonStart!!.measuredWidth
            } else {
                rightBorder += buttonStart!!.measuredWidth
            }

            buttonStart!!.layout(leftBorder, top, rightBorder, bottom)
        }

        if (contentView?.visibility == View.VISIBLE) {
            if (rtl) {
                rightBorder = leftBorder
                leftBorder -= contentView!!.measuredWidth
            } else {
                leftBorder = rightBorder
                rightBorder += contentView!!.measuredWidth
            }

            contentView!!.layout(leftBorder, top, rightBorder, bottom)
        }
    }

    internal fun layoutEnd(left: Int, right: Int, top: Int, bottom: Int, rtl: Boolean) {
        var leftBorder = getEndLeftBorder(left, right, rtl)
        var rightBorder = leftBorder

        for (i in buttonsEnd.size - 1 downTo 0) {
            val button = buttonsEnd[i]

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

            if (buttonStart!!.visibility == View.VISIBLE) {
                leftBorder += buttonParams.rightPadding
            }
        } else {
            leftBorder = left + paddingStart

            if (buttonStart!!.visibility == View.VISIBLE) {
                leftBorder -= buttonParams.leftPadding
            }
        }

        return leftBorder
    }

    internal fun getEndLeftBorder(left: Int, right: Int, rtl: Boolean): Int {
        return if (rtl) {
            left + paddingEnd - buttonParams.rightPadding
        } else {
            right - paddingEnd + buttonParams.leftPadding
        }
    }

    private fun createButton(): NavigationBarButton {
        return NavigationBarButton(context, buttonParams).apply {
            id = View.generateViewId()
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            setOnClickListener(this@NavigationBar)
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

    fun setTitleTextRes(@StringRes textRes: Int) {
        val text = resources.getString(textRes)
        titleTextRes = textRes
        setTitleText(text, true)
    }

    fun resetButtonsEnd() {
        for (button in buttonsEnd) {
            button!!.resetView()
        }
    }

    fun resetView() {
        buttonStart!!.resetView()
        setTitleText(null)
        setContentView(null)
        resetButtonsEnd()
    }
}