package ru.sudox.design.buttons.spannables

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class FirstLineButtonSpannable(
        val typeface: Typeface,
        val color: Int,
        val size: Float
) : MetricAffectingSpan() {

    override fun updateMeasureState(paint: TextPaint) {
        applyFontParameters(paint)
    }

    override fun updateDrawState(paint: TextPaint) {
        applyFontParameters(paint)
    }

    private fun applyFontParameters(paint: TextPaint) {
        paint.let {
            it.typeface = typeface
            it.textSize = size
            it.color = color
        }
    }
}