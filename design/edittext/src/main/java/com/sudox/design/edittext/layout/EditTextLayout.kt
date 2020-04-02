package com.sudox.design.edittext.layout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.design.edittext.R

class EditTextLayout : ViewGroup {

    var errorColor = 0
        set(value) {
            if (errorText != null) {
                errorTextView.setTextColor(value)
                changeStrokeColor(value)
            }

            field = value
            requestLayout()
            invalidate()
        }

    var errorText: String?
        get() = errorTextView.text?.toString()
        set(value) {
            errorTextView.text = value

            changeStrokeColor(if (value != null) {
                errorColor
            } else {
                strokeColor
            })

            requestLayout()
            invalidate()
        }

    var strokeColor: Int = 0
        set(value) {
            field = value

            if (errorText == null) {
                changeStrokeColor(value)
            }

            requestLayout()
            invalidate()
        }

    var strokeWidth: Int = 0
        set(value) {
            field = value

            changeStrokeColor(if (errorText != null) {
                errorColor
            } else {
                strokeColor
            })

            requestLayout()
            invalidate()
        }

    var errorTextTopMargin = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var errorTextVerticalMargin = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var childView: EditText? = null
        set(value) {
            if (field != null) {
                removeView(field)
            }

            field = value?.apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                errorText = errorText // Restore stroke color

                addView(this)
            }

            requestLayout()
            invalidate()
        }

    private var errorTextView = AppCompatTextView(context).apply {
        isSingleLine = true
        maxLines = 1

        this@EditTextLayout.addView(this)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.editTextLayoutStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.EditTextLayout, defStyleAttr, 0).use {
            setTextAppearance(errorTextView, it.getResourceIdOrThrow(R.styleable.EditTextLayout_errorTextAppearance))

            strokeColor = it.getColorOrThrow(R.styleable.EditTextLayout_strokeColor)
            strokeWidth = it.getDimensionPixelSize(R.styleable.EditTextLayout_strokeWidth, 0)
            errorTextVerticalMargin = it.getDimensionPixelSize(R.styleable.EditTextLayout_errorTextVerticalMargin, 0)
            errorTextTopMargin = it.getDimensionPixelSize(R.styleable.EditTextLayout_errorTextTopMargin, 0)
            errorColor = it.getColorOrThrow(R.styleable.EditTextLayout_errorColor)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(childView as View, widthMeasureSpec, heightMeasureSpec)

        val errorTextWidth = MeasureSpec.makeMeasureSpec(
                (childView as View).measuredWidth - 2 * errorTextVerticalMargin, MeasureSpec.EXACTLY
        )

        errorTextView.measure(errorTextWidth, heightMeasureSpec)

        val needWidth = (childView as View).measuredWidth +
                paddingLeft +
                paddingRight

        val needHeight = paddingTop +
                (childView as View).measuredHeight +
                errorTextTopMargin +
                errorTextView.measuredHeight +
                paddingBottom

        setMeasuredDimension(needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val childTopBorder = paddingTop
        val childBottomBorder = childTopBorder + (childView as View).measuredHeight
        val childLeftBorder = paddingLeft
        val childRightBorder = childLeftBorder + (childView as View).measuredWidth

        (childView as View).layout(childLeftBorder, childTopBorder, childRightBorder, childBottomBorder)

        val errorTopBorder = childBottomBorder + errorTextTopMargin
        val errorBottomBorder = errorTopBorder + errorTextView.measuredHeight
        val errorLeftBorder = childLeftBorder + errorTextVerticalMargin
        val errorRightBorder = errorLeftBorder + errorTextView.measuredWidth

        errorTextView.layout(errorLeftBorder, errorTopBorder, errorRightBorder, errorBottomBorder)
    }

    private fun changeStrokeColor(color: Int) {
        childView?.let {
            (it.background as GradientDrawable).setStroke(strokeWidth, color)
            it.invalidate()
        }
    }
}