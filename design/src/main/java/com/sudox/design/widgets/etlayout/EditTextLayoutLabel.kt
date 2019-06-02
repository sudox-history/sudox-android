package com.sudox.design.widgets.etlayout

import android.graphics.Canvas
import android.graphics.Paint
import android.widget.EditText

class EditTextLayoutLabel(val editText: EditText, val params: EditTextLayoutLabelParams) {

    internal var originalText: String? = null
    internal var errorText: String? = null
    internal var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        configurePaint()
    }

    fun dispatchDraw(canvas: Canvas) {
        val text = getCurrentText() ?: return
        val textColor = getCurrentColor()
        val x = editText.compoundPaddingLeft.toFloat()
        val y = getHeight().toFloat()

        paint.color = textColor
        canvas.drawText(text, x, y, paint)
    }

    internal fun configurePaint() {
        paint.typeface = params.textTypeface
        paint.textSize = params.textSize.toFloat()
    }

    internal fun getCurrentColor(): Int {
        return if (editText.isEnabled && errorText != null) {
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
}