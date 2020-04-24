package ru.sudox.design.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getFontOrThrow
import androidx.core.content.res.use
import ru.sudox.design.buttons.spannables.FirstLineButtonSpannable

class TwoLabeledButton : AppCompatTextView {

    var firstLineTypeface: Typeface? = null
        set(value) {
            field = value
            updateText()
        }

    var firstLineTextSize = 0F
        set(value) {
            field = value
            updateText()
        }

    var firstLineTextColor = 0
        set(value) {
            field = value
            updateText()
        }

    var firstLineText: String? = null
        set(value) {
            field = value
            updateText()
        }

    var secondLineText: String? = null
        set(value) {
            field = value
            updateText()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.twoLabeledButtonStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.TwoLabeledButton, defStyleAttr, 0).use {
            firstLineTypeface = it.getFontOrThrow(R.styleable.TwoLabeledButton_firstLineFontFamily)
            firstLineTextSize = it.getDimensionPixelSizeOrThrow(R.styleable.TwoLabeledButton_firstLineTextSize).toFloat()
            firstLineTextColor = it.getColorOrThrow(R.styleable.TwoLabeledButton_firstLineTextColor)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateText() {
        if (firstLineTypeface != null && firstLineText != null && secondLineText != null) {
            text = SpannableStringBuilder("$firstLineText\n$secondLineText").apply {
                setSpan(FirstLineButtonSpannable(
                        firstLineTypeface!!,
                        firstLineTextColor,
                        firstLineTextSize
                ), 0, firstLineText!!.length, 0)
            }
        }
    }
}