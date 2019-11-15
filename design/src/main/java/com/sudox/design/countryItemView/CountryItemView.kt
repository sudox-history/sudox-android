package com.sudox.design.countryItemView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.graphics.withTranslation
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.design.R
import kotlin.math.max
import kotlin.math.min

class CountryItemView : ViewGroup {

    private var flagMargin = 0
    private var flagDrawable: Drawable? = null
    private var flagHeight = 0
    private var flagWidth = 0

    private val nameTextView = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        )

        addView(this)
    }

    private val codeTextView = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        )

        addView(this)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.countryItemViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.CountryItemView, defStyleAttr, 0).use {
            flagMargin = it.getDimensionPixelSize(R.styleable.CountryItemView_flagMargin, 0)
            flagHeight = it.getDimensionPixelSize(R.styleable.CountryItemView_flagHeight, 0)
            flagWidth = it.getDimensionPixelSize(R.styleable.CountryItemView_flagWidth, 0)

            it.getResourceIdOrThrow(R.styleable.CountryItemView_textAppearance).let { id ->
                setTextAppearance(nameTextView, id)
                setTextAppearance(codeTextView, id)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun setCode(code: Int) {
        codeTextView.text = "+${code}"
    }

    fun setName(@StringRes id: Int) {
        nameTextView.setText(id)
    }

    fun setFlag(@DrawableRes id: Int) {
        flagDrawable = context.getDrawable(id)!!.apply {
            setBounds(0, 0, flagWidth, flagHeight)
        }

        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        measureChild(nameTextView, widthMeasureSpec, heightMeasureSpec)
        measureChild(codeTextView, widthMeasureSpec, heightMeasureSpec)

        val needWidth = paddingLeft + flagWidth + flagMargin + nameTextView.measuredWidth + codeTextView.measuredWidth
        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            availableWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            min(needWidth, availableWidth)
        } else {
            needWidth
        }

        val needHeight = paddingTop + max(max(flagHeight, nameTextView.measuredHeight), codeTextView.measuredHeight) + paddingBottom
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
        val textViewTopBorder = paddingTop
        val textViewBottomBorder = textViewTopBorder + nameTextView.measuredHeight

        val nameTextViewLeftBorder = paddingLeft + flagWidth + flagMargin
        val nameTextViewRightBorder = nameTextViewLeftBorder + nameTextView.measuredWidth
        val codeTextViewRightBorder = right - left - paddingRight
        val codeTextViewLeftBorder = codeTextViewRightBorder - codeTextView.measuredWidth

        nameTextView.layout(nameTextViewLeftBorder, textViewTopBorder, nameTextViewRightBorder, textViewBottomBorder)
        codeTextView.layout(codeTextViewLeftBorder, textViewTopBorder, codeTextViewRightBorder, textViewBottomBorder)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        canvas.withTranslation(
                x = paddingLeft.toFloat(),
                y = paddingTop.toFloat()
        ) {
            flagDrawable!!.draw(canvas)
        }
    }
}