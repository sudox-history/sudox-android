package com.sudox.design.codeedittext

import android.content.Context
import android.os.Build
import android.os.Parcelable
import android.text.InputType
import android.text.Layout
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.sudox.design.codeedittext.watchers.CodeTextWatcher
import com.sudox.design.edittext.BasicEditText
import com.sudox.design.edittext.layout.EditTextLayout
import com.sudox.design.edittext.layout.EditTextLayoutChild
import com.sudox.design.saveableview.SaveableViewGroup
import kotlin.math.max

class CodeEditText : SaveableViewGroup<CodeEditText, CodeEditTextState>, EditTextLayoutChild {

    var codeFilledCallback: ((String) -> (Unit))? = null
    var digitsCount: Int
        get() = digitEditTexts?.size ?: 0
        set(value) {
            if (digitEditTexts?.size ?: 0 == value) {
                return
            }

            removeAllViewsInLayout()

            digitEditTexts = Array(value) {
                CodeDigitEditText(context).apply {
                    id = View.generateViewId()
                    inputType = InputType.TYPE_CLASS_NUMBER or
                            InputType.TYPE_NUMBER_FLAG_DECIMAL or
                            InputType.TYPE_NUMBER_FLAG_SIGNED

                    gravity = Gravity.CENTER
                    isSingleLine = true
                    maxLines = 1

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
                        hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
                    }

                    CodeTextWatcher(this, it, this@CodeEditText).apply {
                        addTextChangedListener(this)
                        setOnKeyListener(this)
                    }

                    this@CodeEditText.addView(this)
                }
            }
        }

    internal var digitEditTexts: Array<BasicEditText>? = null
    internal var isPositioningEnabled = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        digitEditTexts?.forEach {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
        }

        val needWidth = MeasureSpec.getSize(widthMeasureSpec)
        val needHeight = paddingTop + (digitEditTexts?.get(0)?.measuredHeight ?: 0) + paddingBottom

        setMeasuredDimension(needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val digitsWidth = digitEditTexts?.sumBy { it.measuredWidth } ?: 0
        val digitsMargin = (measuredWidth - digitsWidth) / max(digitsCount - 1, 0)
        val digitHeight = digitEditTexts?.get(0)?.measuredHeight ?: 0
        val digitWidth = digitEditTexts?.get(0)?.measuredWidth ?: 0

        val topBorder = paddingTop
        val bottomBorder = topBorder + digitHeight
        var leftBorder = paddingLeft
        var rightBorder: Int

        digitEditTexts?.forEach {
            rightBorder = leftBorder + digitWidth
            it.layout(leftBorder, topBorder, rightBorder, bottomBorder)
            leftBorder = rightBorder + digitsMargin
        }
    }

    override fun changeStrokeColor(layout: EditTextLayout, width: Int, color: Int) {
        if (parent is EditTextLayout) {
            digitEditTexts!!.forEach {
                it.changeStrokeColor(parent as EditTextLayout, width, color)
            }

            invalidate()
        }
    }

    fun getCode(): String? {
        val builder = StringBuilder()

        digitEditTexts!!.forEach {
            if (it.text.isNullOrEmpty()) {
                return null
            }

            builder.append(it.text)
        }

        return builder.toString()
    }

    fun notifyThatCodeEntered() {
        val code = getCode() ?: return
        val lastDigitEditText = digitEditTexts!!.last()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // TODO: Replace to common method

        inputMethodManager.hideSoftInputFromWindow(lastDigitEditText.windowToken, 0)
        lastDigitEditText.requestFocus()
        codeFilledCallback?.invoke(code)
    }

    fun changeFocusedDigit(index: Int) {
        val digitEditText = digitEditTexts!!.elementAtOrNull(index)

        if (digitEditText != null) {
            if (digitEditText.text.toString().isNotEmpty()) {
                digitEditText.setSelection(1)
            }

            digitEditText.requestFocus()
        }
    }

    override fun createStateInstance(superState: Parcelable): CodeEditTextState {
        return CodeEditTextState(superState)
    }
}