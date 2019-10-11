package com.sudox.design.codeEditText

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText

class CodeDigitTextWatcher(
        val digitEditText: EditText,
        val digitEditTextIndex: Int,
        val codeEditText: CodeEditText
) : TextWatcher, View.OnKeyListener {

    override fun onKey(view: View, keyCode: Int, event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) {
            return true
        }

        if (event.keyCode == KeyEvent.KEYCODE_DEL) {
            codeEditText.digitsEditTexts!![digitEditTextIndex - 1].requestFocus()
        } else if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
            codeEditText.onCodeCompleted()
        } else if (digitEditText.text.isNotEmpty()) {
            // First symbol will be removed
            digitEditText.setSelection(1)
        }

        return false
    }

    override fun afterTextChanged(source: Editable) {
        if (source.isEmpty()) {
            return
        }

        if (codeEditText.isPositioningEnabled && filterSource(source)) {
            if (digitEditTextIndex == codeEditText.digitsEditTexts!!.lastIndex) {
                codeEditText.onCodeCompleted()
            } else {
                codeEditText
                        .digitsEditTexts!!
                        .elementAtOrNull(digitEditTextIndex + 1)
                        ?.requestFocus()
            }
        }
    }

    private fun filterSource(source: Editable): Boolean {
        if (source.length > 1) {
            source.delete(0, source.length - 1)
        }

        if (source.isNotEmpty() && !source[0].isDigit()) {
            source.delete(0, 1)
        }

        return source.isNotEmpty()
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
}