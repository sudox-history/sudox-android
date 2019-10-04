package com.sudox.design.phoneEditText.countryCodeSelector

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.use
import com.sudox.design.R
import kotlin.math.max
import kotlin.math.min

class CountryCodeSelector : View {

    var codePaint: Paint? = null
    private val codeBounds = Rect()
    private var code: String? = null

    internal var flagDrawableResId = 0
    private var flagDrawable: Drawable? = null
    private var flagWidth = 0
    private var flagHeight = 0
    private var flagMargin = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.countryCodeSelectorStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.CountryCodeSelector, defStyleAttr, 0).use {
            flagWidth = it.getDimensionPixelSizeOrThrow(R.styleable.CountryCodeSelector_flagWidth)
            flagHeight = it.getDimensionPixelSizeOrThrow(R.styleable.CountryCodeSelector_flagHeight)
            flagMargin = it.getDimensionPixelSizeOrThrow(R.styleable.CountryCodeSelector_flagMargin)
        }
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val state = parcelable as CountryCodeSelectorState

        state.apply {
            super.onRestoreInstanceState(state.superState)
            state.readToView(this@CountryCodeSelector)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        return CountryCodeSelectorState(superState!!).apply {
            writeFromView(this@CountryCodeSelector)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        val needWidth = paddingLeft + flagWidth + flagMargin + codeBounds.width() + paddingRight
        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            availableWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            min(needWidth, availableWidth)
        } else {
            needWidth
        }

        val needHeight = paddingTop + max(codeBounds.height(), flagHeight) + paddingBottom
        val measuredHeight = if (heightMode == MeasureSpec.EXACTLY) {
            availableHeight
        } else if (heightMode == MeasureSpec.AT_MOST) {
            min(needHeight, availableHeight)
        } else {
            needHeight
        }

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        val countryCodeX = paddingLeft + flagDrawable!!.bounds.width() + flagMargin.toFloat()
        val countryCodeY = height / 2 - codeBounds.exactCenterY()
        canvas.drawText(code!!, countryCodeX, countryCodeY, codePaint!!)

        val flagX = paddingLeft.toFloat()
        val flagY = height / 2 - flagDrawable!!.bounds.exactCenterY()
        canvas.translate(flagX, flagY)
        flagDrawable!!.draw(canvas)
    }

    fun get(): String? {
        return code?.removePrefix("+")
    }

    fun set(code: String, flagDrawableResId: Int) {
        this.code = "+${code}"
        this.codePaint!!.getTextBounds(this.code, 0, this.code!!.length, codeBounds)

        this.flagDrawableResId = flagDrawableResId
        this.flagDrawable = ContextCompat.getDrawable(context, flagDrawableResId)!!.apply {
            setBounds(0, 0, flagWidth, flagHeight)
        }

        requestLayout()
        invalidate()
    }
}