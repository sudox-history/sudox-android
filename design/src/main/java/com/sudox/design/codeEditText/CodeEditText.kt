package com.sudox.design.codeEditText

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.getIntegerOrThrow
import androidx.core.content.res.use
import com.sudox.design.R
import com.sudox.design.editTextLayout.EditTextLayoutChild
import com.sudox.design.showSoftKeyboard
import kotlin.math.min

class CodeEditText : ViewGroup, EditTextLayoutChild {

    var codeFilledCallback: ((String) -> (Unit))? = null
    var digitsEditTexts: Array<AppCompatEditText>? = null
    var isPositioningEnabled = true

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.codeEditTextStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.CodeEditText, defStyleAttr, 0).use {
            digitsEditTexts = Array(it.getIntegerOrThrow(R.styleable.CodeEditText_digitsCount)) { index ->
                CodeDigitEditText(context, this, index)
            }
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

        val needWidth = paddingLeft + digitsWidth + paddingRight
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
        val digitWidth = digitsEditTexts!![0].measuredWidth
        var rightBorder: Int
        var leftBorder = paddingLeft
        val topBorder = paddingTop
        val bottomBorder = topBorder + digitHeight

        val width = right - left
        val freeWidth = width - digitsEditTexts!!.size * digitWidth
        val digitsMargin = freeWidth / (digitsEditTexts!!.size - 1)

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

    fun showSoftKeyboard() {
        val focusedDigitEditText = digitsEditTexts!!.find {
            it.isFocused
        } ?: digitsEditTexts!!.first()

        if (focusedDigitEditText == digitsEditTexts!!.last() && focusedDigitEditText.text.isNullOrEmpty()) {
            return
        }

        focusedDigitEditText.showSoftKeyboard()
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

    internal fun notifyThatCodeEntered() {
        val code = getCode() ?: return
        val lastDigitEditText = digitsEditTexts!!.last()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(lastDigitEditText.windowToken, 0)
        lastDigitEditText.requestFocus()
        codeFilledCallback?.invoke(code)
    }

    override fun getInstance(): View {
        return this
    }

    override fun setStroke(width: Int, color: Int) {
        digitsEditTexts!!
                .map { it.background as GradientDrawable }
                .forEach { it.setStroke(width, color) }

        invalidate()
    }

    internal fun changeFocusedDigit(index: Int) {
        val digitEditText = digitsEditTexts!!.elementAtOrNull(index)

        if (digitEditText != null) {
            if (digitEditText.text.toString().isNotEmpty()) {
                digitEditText.setSelection(1)
            }

            digitEditText.requestFocus()
        }
    }
}