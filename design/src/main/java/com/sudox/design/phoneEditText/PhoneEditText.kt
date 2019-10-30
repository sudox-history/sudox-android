package com.sudox.design.phoneEditText

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.telephony.PhoneNumberUtils
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.use
import androidx.core.view.updatePadding
import com.sudox.design.R
import com.sudox.design.phoneEditText.countryCodeSelector.CountryCodeSelector
import com.sudox.design.phoneNumberUtil
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlin.math.max
import kotlin.math.min

class PhoneEditText : ViewGroup {

    internal var phoneTextWatcher = PhoneTextWatcher()

    val countryCodeSelector = CountryCodeSelector(context).apply {
        id = View.generateViewId()
    }

    val numberEditText = AppCompatEditText(context).apply {
        addTextChangedListener(phoneTextWatcher)

        id = View.generateViewId()
        inputType = InputType.TYPE_CLASS_PHONE
        imeOptions = EditorInfo.IME_ACTION_DONE
        isSingleLine = true
        maxLines = 1
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

    fun setCountry(regionCode: String, countryCode: String, flagResId: Int, reset: Boolean = true) {
        if (reset) {
            numberEditText.text = null
        }

        countryCodeSelector.set(countryCode, flagResId)
        phoneTextWatcher.setRegionCode(regionCode)

        val exampleNumber = phoneNumberUtil!!.getExampleNumber(regionCode)
        val formattedExampleNumber = phoneNumberUtil!!
                .format(exampleNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
                .removePrefix("${countryCodeSelector.code} ")

        numberEditText.hint = formattedExampleNumber
        invalidate()
    }

    fun getPhoneNumber(): String? {
        val countryCode = countryCodeSelector.get() ?: return null
        val formattedPhone = numberEditText.text ?: return null
        val unformattedPhone = PhoneNumberUtils.stripSeparators(formattedPhone.toString())

        if (unformattedPhone.isEmpty()) {
            return null
        }

        return "$countryCode$unformattedPhone"
    }

    fun getRegionCode(): String? {
        return phoneTextWatcher.regionCode
    }
}