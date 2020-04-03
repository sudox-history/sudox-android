package com.sudox.messenger.android.countries.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Parcelable
import android.text.InputType
import android.text.Layout
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.view.updatePadding
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.design.edittext.layout.EditTextLayout
import com.sudox.design.edittext.layout.EditTextLayoutChild
import com.sudox.design.saveableview.SaveableViewGroup
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.countries.R
import com.sudox.messenger.android.countries.inject.CountriesComponent
import com.sudox.messenger.android.countries.views.state.PhoneEditTextState
import com.sudox.messenger.android.countries.views.watchers.PhoneTextWatcher
import com.sudox.messenger.android.countries.vos.CountryVO
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import javax.inject.Inject
import kotlin.math.max

class PhoneEditText : SaveableViewGroup<PhoneEditText, PhoneEditTextState>, EditTextLayoutChild {

    private var separatorColor: Int
        get() = separatorPaint.color
        set(value) {
            separatorPaint.color = value
            invalidate()
        }

    var separatorHeight: Int
        get() = separatorBounds.height()
        set(value) {
            separatorBounds.let {
                it.set(it.left, 0, it.right, value)
            }

            requestLayout()
            invalidate()
        }

    var separatorWidth: Int
        get() = separatorBounds.width()
        set(value) {
            separatorBounds.let {
                it.set(0, it.top, value, it.bottom)
            }

            requestLayout()
            invalidate()
        }

    var marginBetweenFlagAndCode: Int
        get() = countrySelector.compoundDrawablePadding
        set(value) {
            countrySelector.compoundDrawablePadding = value

            requestLayout()
            invalidate()
        }

    var vo: CountryVO? = null
        set(value) {
            @SuppressLint("SetTextI18n")
            if (value != null) {
                val exampleNumber = phoneNumberUtil!!.getExampleNumber(value.regionCode)

                editText.hint = phoneNumberUtil!!
                        .format(exampleNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
                        .removePrefix("+${value.countryCode} ")

                phoneTextWatcher?.setCountry(value.regionCode, value.countryCode)
                countrySelector.setCompoundDrawablesWithIntrinsicBounds(value.flagId, 0, 0, 0)
                countrySelector.text = "+${value.countryCode}"
            }

            field = value
            requestLayout()
            invalidate()
        }

    var autofillMyNumber = false
        set(value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (value) {
                    editText.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_YES_EXCLUDE_DESCENDANTS
                    editText.setAutofillHints(View.AUTOFILL_HINT_PHONE)
                } else {
                    editText.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
                }

                field = value
            }
        }

    private var separatorPaint = Paint()
    private var separatorBounds = Rect()
    private var countrySelector = AppCompatTextView(context).apply {
        gravity = Gravity.CENTER_VERTICAL
        isFocusable = true
        isClickable = true
        isSingleLine = true
        maxLines = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
            hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        }

        this@PhoneEditText.addView(this)
    }

    internal var editText = PhoneNumberEditText(context).apply {
        id = View.generateViewId()
        inputType = InputType.TYPE_CLASS_PHONE
        imeOptions = EditorInfo.IME_ACTION_DONE
        isSingleLine = true
        maxLines = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
            hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        }

        this@PhoneEditText.background = background
        this@PhoneEditText.addView(this)

        background = null
    }

    @Inject
    @JvmField
    var phoneNumberUtil: PhoneNumberUtil? = null
    var phoneTextWatcher: PhoneTextWatcher? = null
        @Inject
        set(value) {
            if (field != null) {
                editText.removeTextChangedListener(field)
            }

            if (value != null) {
                editText.addTextChangedListener(value.apply {
                    if (vo != null) {
                        setCountry(vo!!.regionCode, vo!!.countryCode)
                    }
                })
            }

            field = value
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.phoneEditTextStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.PhoneEditText, defStyleAttr, 0).use {
            setTextAppearance(countrySelector, it.getResourceIdOrThrow(R.styleable.PhoneEditText_countrySelectorTextAppearance))

            separatorColor = it.getColorOrThrow(R.styleable.PhoneEditText_separatorColor)
            separatorHeight = it.getDimensionPixelSizeOrThrow(R.styleable.PhoneEditText_separatorHeight)
            separatorWidth = it.getDimensionPixelSizeOrThrow(R.styleable.PhoneEditText_separatorWidth)
            marginBetweenFlagAndCode = it.getDimensionPixelSizeOrThrow(R.styleable.PhoneEditText_marginBetweenFlagAndCode)

            val separatorLeftMargin = it.getDimensionPixelSizeOrThrow(R.styleable.PhoneEditText_separatorLeftMargin)
            val separatorRightMargin = it.getDimensionPixelSizeOrThrow(R.styleable.PhoneEditText_separatorRightMargin)

            autofillMyNumber = it.getBoolean(R.styleable.PhoneEditText_autofillMyNumber, false)
            countrySelector.updatePadding(left = editText.paddingLeft, right = separatorLeftMargin)
            editText.updatePadding(left = separatorRightMargin)
        }

        ((context as CoreActivity).getCoreComponent() as CountriesComponent).inject(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthInSpec = MeasureSpec.getSize(widthMeasureSpec)
        val availableWidth = widthInSpec - paddingLeft - paddingRight - separatorWidth
        val availableWidthSpec = MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.AT_MOST)

        countrySelector.measure(availableWidthSpec, heightMeasureSpec)
        editText.measure(MeasureSpec.makeMeasureSpec(availableWidth - countrySelector.measuredWidth, MeasureSpec.EXACTLY), heightMeasureSpec)

        val needHeight = paddingTop +
                max(max(countrySelector.measuredHeight, editText.measuredHeight), separatorHeight) +
                paddingBottom

        countrySelector.measure(availableWidthSpec, MeasureSpec.makeMeasureSpec(needHeight, MeasureSpec.EXACTLY))

        setMeasuredDimension(widthInSpec, needHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val selectorTopBorder = paddingTop
        val selectorLeftBorder = paddingLeft
        val selectorRightBorder = selectorLeftBorder + countrySelector.measuredWidth
        val selectorBottomBorder = selectorTopBorder + countrySelector.measuredHeight

        countrySelector.layout(selectorLeftBorder, selectorTopBorder, selectorRightBorder, selectorBottomBorder)

        val editTextTopBorder = paddingTop
        val editTextLeftBorder = selectorRightBorder + separatorWidth
        val editTextRightBorder = editTextLeftBorder + editText.measuredWidth
        val editTextBottomBorder = editTextTopBorder + editText.measuredHeight

        editText.layout(editTextLeftBorder, editTextTopBorder, editTextRightBorder, editTextBottomBorder)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        canvas.translate(countrySelector.right.toFloat(), measuredHeight / 2 - separatorBounds.exactCenterY())
        canvas.drawRect(separatorBounds, separatorPaint)
    }

    override fun changeStrokeColor(layout: EditTextLayout, width: Int, color: Int) {
        (background as GradientDrawable).setStroke(width, color)

        separatorPaint.color = if (layout.errorColor != color) {
            separatorColor
        } else {
            color
        }

        invalidate()
    }

    override fun createStateInstance(superState: Parcelable): PhoneEditTextState {
        return PhoneEditTextState(superState)
    }
}