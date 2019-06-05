package com.sudox.design.widgets.etlayout

import android.content.Context
import android.graphics.Canvas
import androidx.annotation.StringRes
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.sudox.design.R

class EditTextLayout : ViewGroup, TextWatcher {

    internal var label: EditTextLayoutLabel? = null
    internal var labelParamsTemp: EditTextLayoutLabelParams? = null
    internal var editText: EditText? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.editTextLayoutStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        with(context.obtainStyledAttributes(attrs, R.styleable.EditTextLayout, defStyleAttr, R.style.EditTextLayout)) {
            labelParamsTemp = EditTextLayoutLabelParams()
            labelParamsTemp!!.readFromAttrs(this, context.theme)
            recycle()
        }
    }

    override fun addView(child: View) {
        if (editText == null && child is EditText) {
            editText = child
            initEditText()
        }

        super.addView(child)
    }

    private fun initEditText() {
        if (editText != null) {
            label = EditTextLayoutLabel(editText!!, labelParamsTemp!!)
            label!!.originalText = editText!!.hint?.toString()

            labelParamsTemp = null
            editText!!.hint = null
            editText!!.addTextChangedListener(this)
        }
    }

    private fun findEditText() {
        editText = (0 until childCount)
                .map { getChildAt(it) }
                .first { it is EditText } as EditText
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        findEditText()
        initEditText()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (editText != null) {
            measureChild(editText!!, widthMeasureSpec, heightMeasureSpec)

            val labelHeight = label!!.getHeight()
            val measuredHeight = labelHeight + editText!!.measuredHeight
            val measuredHeightSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)

            setMeasuredDimension(widthMeasureSpec, measuredHeightSpec)
        } else {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (editText != null) {
            val topBorder = label!!.getHeight()
            val bottomBorder = topBorder + editText!!.measuredHeight
            val rightBorder = editText!!.measuredWidth

            editText!!.layout(0, topBorder, rightBorder, bottomBorder)
        }
    }

    override fun onDetachedFromWindow() {
        editText?.removeTextChangedListener(this)
        super.onDetachedFromWindow()
    }

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
        if (before == 0 && count == 0) {
            return
        }

        resetErrorText()
    }

    override fun dispatchDraw(canvas: Canvas) {
        label?.dispatchDraw(canvas)
        super.dispatchDraw(canvas)
    }

    override fun childDrawableStateChanged(child: View) {
        if (child == editText) {
            invalidate()
        }
    }

    fun setErrorText(text: String?) {
        label?.errorText = text
        invalidate()
    }

    fun setErrorTextRes(@StringRes textRes: Int) {
        val text = resources.getText(textRes)
        setErrorText(text.toString())
    }

    fun resetErrorText() {
        if (label?.errorText != null) {
            setErrorText(null)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {}
}