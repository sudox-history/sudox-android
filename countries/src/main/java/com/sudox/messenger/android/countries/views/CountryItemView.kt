package com.sudox.messenger.android.countries.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.messenger.android.countries.R
import com.sudox.messenger.android.countries.vos.CountryVO
import kotlin.math.max

class CountryItemView : ViewGroup {

    var marginBetweenFlagAndCountryName = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var vo: CountryVO? = null
        @SuppressLint("SetTextI18n")
        set(value) {
            if (value != null) {
                nameTextView.setText(value.nameId)
                codeTextView.text = "+${value.countryCode}"
                flagImageView.setImageResource(value.flagId)
            } else {
                nameTextView.text = null
                codeTextView.text = null
                flagImageView.setImageDrawable(null)
            }

            field = value
            requestLayout()
            invalidate()
        }

    private var nameTextView = AppCompatTextView(context).apply {
        this@CountryItemView.addView(this)
    }

    private var codeTextView = AppCompatTextView(context).apply {
        this@CountryItemView.addView(this)
    }

    private var flagImageView = AppCompatImageView(context).apply {
        scaleType = ImageView.ScaleType.CENTER_CROP

        this@CountryItemView.addView(this)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.countryItemViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.CountryItemView, defStyleAttr, 0).use {
            marginBetweenFlagAndCountryName = it.getDimensionPixelSize(R.styleable.CountryItemView_marginBetweenFlagAndName, 0)

            setTextAppearance(nameTextView, it.getResourceIdOrThrow(R.styleable.CountryItemView_nameTextAppearance))
            setTextAppearance(codeTextView, it.getResourceIdOrThrow(R.styleable.CountryItemView_codeTextAppearance))

            flagImageView.layoutParams = LayoutParams(
                    it.getDimensionPixelSizeOrThrow(R.styleable.CountryItemView_flagWidth),
                    it.getDimensionPixelSizeOrThrow(R.styleable.CountryItemView_flagHeight)
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(nameTextView, widthMeasureSpec, heightMeasureSpec)
        measureChild(codeTextView, widthMeasureSpec, heightMeasureSpec)
        measureChild(flagImageView, widthMeasureSpec, heightMeasureSpec)

        val needHeight = paddingTop +
                max(max(nameTextView.measuredHeight, codeTextView.measuredHeight), flagImageView.measuredHeight) +
                paddingBottom

        val needWidth = MeasureSpec.getSize(widthMeasureSpec)

        setMeasuredDimension(needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val flagLeftBorder = paddingLeft
        val flagRightBorder = flagLeftBorder + flagImageView.measuredWidth
        val flagTopBorder = measuredHeight / 2 - flagImageView.measuredHeight / 2
        val flagBottomBorder = flagTopBorder + flagImageView.measuredHeight

        flagImageView.layout(flagLeftBorder, flagTopBorder, flagRightBorder, flagBottomBorder)

        val nameLeftBorder = flagRightBorder + marginBetweenFlagAndCountryName
        val nameRightBorder = nameLeftBorder + nameTextView.measuredWidth
        val nameTopBorder = measuredHeight / 2 - nameTextView.measuredHeight / 2
        val nameBottomBorder = nameTopBorder + nameTextView.measuredHeight

        nameTextView.layout(nameLeftBorder, nameTopBorder, nameRightBorder, nameBottomBorder)

        val codeRightBorder = measuredWidth - paddingRight
        val codeLeftBorder = codeRightBorder - codeTextView.measuredWidth
        val codeTopBorder = measuredHeight / 2 - codeTextView.measuredHeight / 2
        val codeBottomBorder = codeTopBorder + codeTextView.measuredHeight

        codeTextView.layout(codeLeftBorder, codeTopBorder, codeRightBorder, codeBottomBorder)
    }
}