package com.sudox.design.widgets.etlayout.label

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.widget.EditText
import com.sudox.design.helpers.isTextRtl

class EditTextLayoutLabel(val editText: EditText, val params: EditTextLayoutLabelParams) {
    internal var originalText: String? = null
    internal var errorText: String? = null
    internal var bounds = Rect()
    internal var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        configurePaint()
    }

    fun dispatchDraw(canvas: Canvas) {
        val text = getCurrentText() ?: return
        val textColor = getCurrentColor()
        val x = getXCoord(text).toFloat()
        val y = getHeight().toFloat()

        paint.color = textColor
        canvas.drawText(text, x, y, paint)
    }

    internal fun configurePaint() {
        paint.typeface = params.textTypeface
        paint.textSize = params.textSize.toFloat()
    }

    internal fun getXCoord(text: String): Int {
        return if (editText.isTextRtl(text)) {
            paint.getTextBounds(text, 0, text.length, bounds)
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