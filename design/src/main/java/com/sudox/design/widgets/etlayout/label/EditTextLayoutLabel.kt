package com.sudox.design.widgets.etlayout.label

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.widget.EditText
import com.sudox.design.helpers.isTextRtl

class EditTextLayoutLabel(val editText: EditText, val params: EditTextLayoutLabelParams) {

    internal var originalText: String? = null
    internal var errorTextRes: Int = 0
    internal var errorText: String? = null

    internal var bounds = Rect()
    internal var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun draw(canvas: Canvas) {
        val text = getCurrentText() ?: return
        val color = getCurrentColor()
        val x = getX(text).toFloat()
        val y = getHeight().toFloat()

        paint.color = color
        canvas.drawText(text, x, y, paint)
    }

    internal fun configurePaint() {
        paint.typeface = params.textTypeface
        paint.textSize = params.textSize.toFloat()
    }

    internal fun getX(text: String): Int {
        paint.getTextBounds(text, 0, text.length, bounds)

        return if (editText.isTextRtl(text)) {
            editText.measuredWidth - editText.compoundPaddingStart - bounds.width()
        } else {
            editText.compoundPaddingStart
        }
    }

    internal fun getCurrentColor(): Int {
        return if (needShowingError()) {
            params.errorTextColor
        } else if (isEditTextActive()) {
            editText.currentTextColor
        } else {
            editText.currentHintTextColor
        }
    }

    internal fun getCurrentText(): String? {
        return if (editText.isEnabled && errorText != null) {
            errorText
        } else {
            originalText
        }
    }

    internal fun getHeight(): Int {
        return paint.textSize.toInt()
    }

    internal fun isEditTextActive(): Boolean {
        return (editText.isFocused || editText.isPressed) && editText.isEnabled
    }

    internal fun needShowingError(): Boolean {
        return editText.isEnabled && errorText != null
    }
}