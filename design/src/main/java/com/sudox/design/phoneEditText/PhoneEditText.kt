package com.sudox.design.phoneEditText

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.graphics.drawable.GradientDrawable
import android.os.Parcelable
import android.telephony.PhoneNumberUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.use
import androidx.core.view.updatePadding
import com.sudox.design.R
import com.sudox.design.phoneEditText.childs.PhoneCountryCodeSelector
import com.sudox.design.phoneEditText.childs.PhoneNumberEditText
import com.sudox.design.editTextLayout.EditTextLayoutChild
import kotlin.math.max
import kotlin.math.min

class PhoneEditText : ViewGroup, EditTextLayoutChild {

    var regionFlagIdCallback: ((String) -> (Int))? = null

    internal val countryCodeSelector = PhoneCountryCodeSelector(context)
    internal val numberEditText = PhoneNumberEditText(context).apply {
        id = View.generateViewId()
    }

    private var separatorDrawable: Drawable? = null
    private var separatorLeftMargin = 0
    private var separatorRightMargin = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.phoneEditTextStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.PhoneEditText, defStyleAttr, 0).use {
            separatorDrawable = it.getDrawableOrThrow(R.styleable.PhoneEditText_separatorDrawable).apply {
                val height = it.getDimensionPixelSizeOrThrow(R.styleable.PhoneEditText_separatorDrawableHeight)
                val width = it.getDimensionPixelSizeOrThrow(R.styleable.PhoneEditText_separatorDrawableWidth)

                setBounds(0, 0, width, height)
            }

            separatorLeftMargin = it.getDimensionPixelSizeOrThrow(R.styleable.PhoneEditText_separatorLeftMargin)
            separatorRightMargin = it.getDimensionPixelSizeOrThrow(R.styleable.PhoneEditText_separatorRightMargin)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (it.getBoolean(R.styleable.PhoneEditText_autofillMyNumber, false)) {
                    numberEditText.setAutofillHints(View.AUTOFILL_HINT_PHONE)
                } else {
                    numberEditText.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
                }
            }
        }

        addView(countryCodeSelector)
        addView(numberEditText)

        inheritEditTextParameters()
    }

    private fun inheritEditTextParameters() {
        clipToOutline = true // Fix for ripple-effect's borders
        background = numberEditText.background

        countryCodeSelector.apply {
            updatePadding(left = numberEditText.paddingLeft, right = separatorLeftMargin)

            codePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                set(numberEditText.paint)
            }
        }

        numberEditText.apply {
            updatePadding(left = separatorRightMargin)
            background = null
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        // Width mode always equals AT_MOST
        measureChild(countryCodeSelector, widthMeasureSpec, heightMeasureSpec)

        val numberEditTextWidthSpec = if (widthMode != MeasureSpec.UNSPECIFIED) {
            val numberEditTextWidth = availableWidth -
                    countryCodeSelector.measuredWidth -
                    separatorDrawable!!.bounds.width() -
                    paddingLeft -
                    paddingRight

            MeasureSpec.makeMeasureSpec(numberEditTextWidth, widthMode)
        } else {
            widthMeasureSpec
        }

        numberEditText.measure(numberEditTextWidthSpec, heightMeasureSpec)

        val needWidth = paddingLeft +
                countryCodeSelector.measuredWidth +
                separatorLeftMargin +
                separatorDrawable!!.bounds.width() +
                separatorRightMargin +
                numberEditText.measuredWidth +
                paddingRight

        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            availableWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            min(needWidth, availableWidth)
        } else {
            needWidth
        }

        val needHeight = paddingTop +
                max(max(numberEditText.measuredHeight, separatorDrawable!!.bounds.height()), countryCodeSelector.measuredHeight) +
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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val height = bottom - top

        countryCodeSelector.layout(paddingLeft, paddingTop, paddingLeft + countryCodeSelector.measuredWidth, height)

        val numberEditTextLeftBorder = paddingLeft +
                countryCodeSelector.measuredWidth +
                separatorDrawable!!.bounds.width()

        numberEditText.layout(numberEditTextLeftBorder, paddingTop, numberEditTextLeftBorder + numberEditText.measuredWidth, height)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        val separatorX = countryCodeSelector.measuredWidth.toFloat()
        val separatorY = measuredHeight / 2 - separatorDrawable!!.bounds.exactCenterY()

        canvas.apply {
            translate(separatorX, separatorY)
            separatorDrawable!!.draw(this)
        }
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val state = parcelable as PhoneEditTextState

        state.apply {
            super.onRestoreInstanceState(state.superState)
            state.readToView(this@PhoneEditText)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        return PhoneEditTextState(superState!!).apply {
            writeFromView(this@PhoneEditText)
        }
    }

    fun setCountry(regionCode: String, countryCode: Int, flagResId: Int, reset: Boolean = true) {
        if (reset) {
            numberEditText.text = null
        }

        countryCodeSelector.set(countryCode, flagResId)
        numberEditText.setCountry(regionCode, countryCode)
        invalidate()
    }

    fun getPhoneNumber(): String? {
        val countryCode = countryCodeSelector.get()

        if (countryCode == 0) {
            return null
        }

        val formattedPhone = numberEditText.text ?: return null
        val unformattedPhone = PhoneNumberUtils.stripSeparators(formattedPhone.toString())

        if (unformattedPhone.isEmpty()) {
            return null
        }

        return "$countryCode$unformattedPhone"
    }

    fun getRegionCode(): String? {
        return numberEditText.getRegionCode()
    }

    override fun setStroke(width: Int, color: Int) {
        (background as GradientDrawable).setStroke(width, color)
    }

    override fun getInstance(): View {
        return this
    }
}