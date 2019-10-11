package com.sudox.design.codeEditText

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getIntegerOrThrow
import androidx.core.content.res.use
import com.sudox.design.R
import kotlin.math.min

class CodeEditText : ViewGroup {

    var codeFilledCallback: ((String) -> (Unit))? = null

    internal var digitsEditTexts: Array<AppCompatEditText>? = null
    internal var isPositioningEnabled = true

    private var digitsMargin = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.codeEditTextStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.CodeEditText, defStyleAttr, 0).use {
            val digitsCount = it.getIntegerOrThrow(R.styleable.CodeEditText_digitsCount)

            digitsMargin = it.getDimensionPixelSizeOrThrow(R.styleable.CodeEditText_digitsMargin)
            digitsEditTexts = Array(digitsCount, ::createDigitEditText)
        }
    }

    private fun createDigitEditText(index: Int): AppCompatEditText {
        return AppCompatEditText(context).apply {
            id = View.generateViewId()
            gravity = Gravity.CENTER
            inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    InputType.TYPE_NUMBER_FLAG_SIGNED

            isSingleLine = true
            maxLines = 1

            CodeDigitTextWatcher(this, index, this@CodeEditText).apply {
                addTextChangedListener(this)
                setOnKeyListener(this)
            }

            addView(this)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        val digitsWidth = digitsEditTexts!!.sumBy {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
            it.measuredWidth
        }

        val needWidth = paddingLeft + digitsWidth + (digitsEditTexts!!.size - 1) * digitsMargin + paddingRight
        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            availableWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            min(needWidth, availableWidth)
        } else {
            needWidth
        }

        val needHeight = paddingTop + digitsEditTexts!![0].measuredHeight + paddingBottom // All digits have same height.
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
        val digitHeight = digitsEditTexts!![0].measuredHeight
        var rightBorder: Int
        var leftBorder = paddingLeft
        val topBorder = paddingTop
        val bottomBorder = topBorder + digitHeight

        digitsEditTexts!!.forEach {
            rightBorder = leftBorder + it.measuredWidth
            it.layout(leftBorder, topBorder, rightBorder, bottomBorder)
            leftBorder = rightBorder + digitsMargin
        }
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val state = parcelable as CodeEditTextState

        state.apply {
            super.onRestoreInstanceState(state.superState)
            state.readToView(this@CodeEditText)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        return CodeEditTextState(superState!!).apply {
            writeFromView(this@CodeEditText)
        }
    }

    fun getCode(): String? {
        val builder = StringBuilder()

        digitsEditTexts!!.forEach {
            if (it.text.isNullOrEmpty()) {
                return null
            }

            builder.append(it.text)
        }

        return builder.toString()
    }

    internal fun onCodeCompleted() {
        codeFilledCallback?.invoke(getCode() ?: return)
    }
}