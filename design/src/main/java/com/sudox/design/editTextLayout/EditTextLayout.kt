package com.sudox.design.editTextLayout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.design.R
import kotlin.math.max
import kotlin.math.min

class EditTextLayout : ViewGroup {

    internal var errorTextId = 0

    private var isErrorShowing = false
    private var errorTextViewTopMargin = 0
    private var errorTextViewLeftMargin = 0
    private var errorTextView = AppCompatTextView(context).apply {
        addView(this)
    }

    private var editText: EditText? = null
    private var strokeColor = 0
    private var strokeWidth = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.editTextLayoutStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.EditTextLayout, defStyleAttr, 0).use {
            errorTextViewTopMargin = it.getDimensionPixelSize(R.styleable.EditTextLayout_errorTextTopMargin, 0)
            errorTextViewLeftMargin = it.getDimensionPixelSize(R.styleable.EditTextLayout_errorTextLeftMargin, 0)
            strokeColor = it.getColorOrThrow(R.styleable.EditTextLayout_strokeColor)
            strokeWidth = it.getDimensionPixelSize(R.styleable.EditTextLayout_strokeWidth, 0)

            setTextAppearance(errorTextView, it.getResourceIdOrThrow(R.styleable.EditTextLayout_errorTextAppearance))
        }
    }

    override fun addView(child: View) {
        if (editText == null && child is EditText) {
            editText = child
        }

        super.addView(child)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        editText = (0 until childCount)
                .map { getChildAt(it) }
                .first { it is EditText } as EditText
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        measureChild(editText!!, widthMeasureSpec, heightMeasureSpec)
        measureChild(errorTextView, widthMeasureSpec, heightMeasureSpec)

        val needWidth = paddingLeft +
                max(editText!!.measuredWidth, errorTextView.measuredWidth + errorTextViewLeftMargin) +
                paddingRight

        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            availableWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            min(needWidth, availableWidth)
        } else {
            needWidth
        }

        val needHeight = paddingTop +
                editText!!.measuredHeight +
                errorTextViewTopMargin +
                errorTextView.measuredHeight +
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

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val editTextTopBorder = paddingTop
        val editTextBottomBorder = editTextTopBorder + editText!!.measuredHeight
        val editTextLeftBorder = paddingLeft
        val editTextRightBorder = editTextLeftBorder + editText!!.measuredWidth

        editText!!.layout(editTextLeftBorder, editTextTopBorder, editTextRightBorder, editTextBottomBorder)

        val errorTextViewTopBorder = editTextBottomBorder + errorTextViewTopMargin
        val errorTextViewBottomBorder = errorTextViewTopBorder + errorTextView.measuredHeight
        val errorTextViewLeftBorder = editTextLeftBorder + errorTextViewLeftMargin
        val errorTextViewRightBorder = errorTextViewLeftBorder + errorTextView.measuredWidth

        errorTextView.layout(errorTextViewLeftBorder, errorTextViewTopBorder, errorTextViewRightBorder, errorTextViewBottomBorder)
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val state = parcelable as EditTextLayoutState

        state.apply {
            super.onRestoreInstanceState(state.superState)
            state.readToView(this@EditTextLayout)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        return EditTextLayoutState(superState!!).apply {
            writeFromView(this@EditTextLayout)
        }
    }

    fun setErrorText(text: String?, fromRes: Boolean = false) {
        errorTextView.text = text
        isErrorShowing = text != null

        if (!fromRes) {
            errorTextId = 0
        }

        val needStrokeColor = if (text != null) {
            errorTextView.currentTextColor
        } else {
            strokeColor
        }

        (editText!!.background as? GradientDrawable)?.setStroke(strokeWidth, needStrokeColor)
    }

    fun setErrorText(@StringRes errorTextId: Int) {
        val text = context.getString(errorTextId)

        this.errorTextId = errorTextId
        this.setErrorText(text, true)
    }

    fun getErrorText(): String? {
        if (!isErrorShowing) {
            return null
        }

        return errorTextView.text.toString()
    }
}