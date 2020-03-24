package com.sudox.design.appbar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Layout
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance

class AppBar : ViewGroup {

    var vo: AppBarVO? = null
        set(value) {
            removeAllViewsInLayout()
            addView(titleTextView)

            val titleId = value?.getTitle() ?: NOT_USED_PARAMETER

            if (titleId != NOT_USED_PARAMETER) {
                titleTextView.setText(titleId)
            } else {
                titleTextView.text = null
            }

            val leftButtons = value?.getButtonsAtLeft()
            val rightButtons = value?.getButtonsAtRight()

            buttonsAtLeft = if (leftButtons != null) {
                Array(leftButtons.size) {
                    createButton(leftButtons[it])
                }
            } else {
                null
            }

            buttonsAtRight = if (rightButtons != null) {
                Array(rightButtons.size) {
                    createButton(rightButtons[it])
                }
            } else {
                null
            }

            field = value
            requestLayout()
            invalidate()
        }

    var buttonsStyleId: Int = 0
        set(value) {
            buttonsAtLeft?.forEach {
                setTextAppearance(it, value)
            }

            buttonsAtRight?.forEach {
                setTextAppearance(it, value)
            }

            field = value
            requestLayout()
            invalidate()
        }

    private var buttonsAtLeft: Array<AppCompatTextView>? = null
    private var buttonsAtRight: Array<AppCompatTextView>? = null
    private var titleTextView = createTextView()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.appbarStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.AppBar, defStyleAttr, 0).use {
            buttonsStyleId = it.getResourceIdOrThrow(R.styleable.AppBar_buttonsStyle)

            setTextAppearance(titleTextView, it.getResourceIdOrThrow(R.styleable.AppBar_titleTextAppearance))
        }
    }

    private fun createTextView(themeId: Int = 0): AppCompatTextView {
        return AppCompatTextView(if (themeId != 0) {
            ContextThemeWrapper(context, themeId)
        } else {
            context
        }).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
                hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
            }

            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            isSingleLine = true
            maxLines = 1

            this@AppBar.addView(this)
        }
    }

    private fun createButton(triple: Triple<Int, Int, Int>): AppCompatTextView {
        return createTextView(buttonsStyleId).apply {
            if (triple.second != NOT_USED_PARAMETER) {
                setCompoundDrawablesWithIntrinsicBounds(triple.second, 0, 0, 0)
            }

            if (triple.third != NOT_USED_PARAMETER) {
                setText(triple.third)
            }

            isFocusable = true
            isClickable = true
            gravity = Gravity.CENTER
            tag = triple.first
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(titleTextView, widthMeasureSpec, heightMeasureSpec)

        buttonsAtLeft?.forEach { measureChild(it, widthMeasureSpec, heightMeasureSpec) }
        buttonsAtRight?.forEach { measureChild(it, widthMeasureSpec, heightMeasureSpec) }

        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (!titleTextView.text.isNullOrEmpty()) {
            val leftBorder = measuredWidth / 2 - titleTextView.measuredWidth / 2
            val rightBorder = leftBorder + titleTextView.measuredWidth
            val topBorder = measuredHeight / 2 - titleTextView.measuredHeight / 2
            val bottomBorder = topBorder + titleTextView.measuredHeight

            titleTextView.layout(leftBorder, topBorder, rightBorder, bottomBorder)
        } else {
            titleTextView.layout(0, 0, 0, 0)
        }

        var leftBorderLeftButton = paddingLeft
        var rightBorderLeftButton = leftBorderLeftButton
        var rightBorderRightButton = measuredWidth - paddingRight
        var leftBorderRightButton = rightBorderRightButton
        val firstButton = (buttonsAtLeft ?: buttonsAtRight)?.first()

        if (firstButton != null) {
            val buttonsTopBorder = measuredHeight / 2 - firstButton.measuredHeight / 2
            val buttonsBottomBorder = buttonsTopBorder + firstButton.measuredHeight

            buttonsAtLeft?.forEach {
                rightBorderLeftButton = leftBorderLeftButton + it.measuredWidth
                it.layout(leftBorderLeftButton, buttonsTopBorder, rightBorderLeftButton, buttonsBottomBorder)
                leftBorderLeftButton = rightBorderLeftButton
            }

            buttonsAtRight?.forEach {
                leftBorderRightButton = rightBorderRightButton - it.measuredWidth
                it.layout(leftBorderRightButton, buttonsTopBorder, rightBorderRightButton, buttonsBottomBorder)
                rightBorderRightButton = leftBorderRightButton
            }
        }
    }
}