package ru.sudox.android.core.ui.code

import android.content.Context
import android.os.Build
import android.os.Parcelable
import android.text.InputType
import android.util.AttributeSet
import android.util.SparseArray
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.autofill.HintConstants
import androidx.core.view.children
import kotlin.math.min

internal const val CODE_LENGTH = 5
internal val DIGITS_IDS = Array(CODE_LENGTH) { View.generateViewId() }

/**
 * Поле ввода для кода.
 */
class CodeEditText : ViewGroup, View.OnKeyListener {

    private var isPositioningDisabledForAll = false
    var blocksFilledCallback: ((String) -> (Unit))? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        repeat(CODE_LENGTH) {
            val editText = AppCompatEditText(context)

            editText.tag = it
            editText.id = DIGITS_IDS[it]
            editText.gravity = Gravity.CENTER_HORIZONTAL
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            editText.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            editText.setOnKeyListener(this)
            editText.addTextChangedListener(
                CodeTextWatcher(
                    it,
                    ::requestDigitFocus,
                    ::setText,
                    ::setTextForAll,
                    ::areBlocksFilled,
                    ::notifyThatBlocksFilled,
                    ::setSelection,
                    ::getSelection,
                    ::isPositioningDisabled
                )
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                editText.setAutofillHints(HintConstants.generateSmsOtpHintForCharacterPosition(it + 1))
            }

            addView(editText)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(widthMeasureSpec)
        val widthSpec = MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.AT_MOST)
        val heightSpec = MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.AT_MOST)

        children.forEach {
            measureChild(it, widthSpec, heightSpec)
        }

        setMeasuredDimension(availableWidth, getChildAt(0).measuredHeight + paddingTop + paddingBottom)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val topBorder = paddingTop
        val firstChild = getChildAt(0)
        val digitWidth = firstChild.measuredWidth
        val digitsWidth = digitWidth * CODE_LENGTH
        val marginHorizontal = (measuredWidth - paddingStart - paddingEnd - digitsWidth) / (CODE_LENGTH - 1)
        val bottomBorder = topBorder + firstChild.measuredHeight

        if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            var rightBorder = measuredWidth - paddingStart
            var leftBorder: Int

            children.forEach {
                leftBorder = rightBorder - digitWidth
                it.layout(leftBorder, topBorder, rightBorder, bottomBorder)
                rightBorder = leftBorder - marginHorizontal
            }
        } else {
            var leftBorder = paddingStart
            var rightBorder: Int

            children.forEach {
                rightBorder = leftBorder + digitWidth
                it.layout(leftBorder, topBorder, rightBorder, bottomBorder)
                leftBorder = rightBorder + marginHorizontal
            }
        }
    }

    override fun onKey(view: View, keyCode: Int, event: KeyEvent): Boolean {
        val currentIndex = focusedChild.tag as Int
        val currentSelection = getSelection(currentIndex)

        if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_DEL && currentIndex > 0 && currentSelection == 0) {
            isPositioningDisabledForAll = true

            if (currentIndex - 1 >= 0) {
                (getChildAt(currentIndex - 1) as EditText).text = null
            }

            requestDigitFocus(currentIndex - 1)
            isPositioningDisabledForAll = false
            return true
        }

        return false
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        isPositioningDisabledForAll = true
        super.dispatchRestoreInstanceState(container)
        isPositioningDisabledForAll = false
    }

    private fun setText(index: Int, text: CharSequence) {
        (getChildAt(index) as EditText).let {
            it.setText(text)
            it.setSelection(1)
        }
    }

    private fun setTextForAll(start: Int, text: CharSequence) {
        val iteratesCount = min(text.length, CODE_LENGTH - start)
        val lastChild = getChildAt(iteratesCount + start - 1)

        isPositioningDisabledForAll = true

        for (i in 0 until iteratesCount) {
            setText(i + start, text[i].toString())

            if (i + start == CODE_LENGTH - 1 && areBlocksFilled()) {
                notifyThatBlocksFilled()
            }
        }

        isPositioningDisabledForAll = false
        lastChild.requestFocus()
    }

    private fun areBlocksFilled(): Boolean {
        children.forEach {
            if ((it as EditText).text.isEmpty()) {
                return false
            }
        }

        return true
    }

    private fun notifyThatBlocksFilled() {
        if (blocksFilledCallback != null) {
            val builder = StringBuilder()

            children.forEach {
                builder.append((it as EditText).text)
            }

            blocksFilledCallback?.invoke(builder.toString())
        }
    }

    private fun setSelection(index: Int, selection: Int) {
        (getChildAt(index) as EditText).setSelection(selection)
    }

    private fun requestDigitFocus(index: Int) {
        getChildAt(index).requestFocus()
    }

    private fun isPositioningDisabled(): Boolean = isPositioningDisabledForAll
    private fun getSelection(index: Int): Int = (getChildAt(index) as EditText).selectionStart
}