package com.sudox.design.editTextLayout

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
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

    private var child: EditTextLayoutChild? = null
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
        if (this.child == null && child is EditTextLayoutChild) {
            this.child = child
        }

        super.addView(child)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        child = (0 until childCount)
                .map { getChildAt(it) }
                .first { it is EditTextLayoutChild } as EditTextLayoutChild
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)
        val childInstance = child!!.getInstance()

        measureChild(childInstance, widthMeasureSpec, heightMeasureSpec)
        measureChild(errorTextView, widthMeasureSpec, heightMeasureSpec)

        val needWidth = paddingLeft +
                max(childInstance.measuredWidth, errorTextView.measuredWidth + errorTextViewLeftMargin) +
                paddingRight

        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            availableWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            min(needWidth, availableWidth)
        } else {
            needWidth
        }

        val needHeight = paddingTop +
                childInstance.measuredHeight +
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
        val childInstance = child!!.getInstance()
        val childTopBorder = paddingTop
        val childBottomBorder = childTopBorder + childInstance.measuredHeight
        val childLeftBorder = paddingLeft
        val childRightBorder = childLeftBorder + childInstance.measuredWidth

        childInstance.layout(childLeftBorder, childTopBorder, childRightBorder, childBottomBorder)

        val errorTextViewTopBorder = childBottomBorder + errorTextViewTopMargin
        val errorTextViewBottomBorder = errorTextViewTopBorder + errorTextView.measuredHeight
        val errorTextViewLeftBorder = childLeftBorder + errorTextViewLeftMargin
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

        child!!.setStroke(strokeWidth, needStrokeColor)
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