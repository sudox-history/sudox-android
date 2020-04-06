package ru.sudox.design.codeedittext.watchers

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import ru.sudox.design.codeedittext.CodeEditText

class CodeTextWatcher(
        val digitEditText: EditText,
        val digitEditTextIndex: Int,
        val codeEditText: CodeEditText
) : TextWatcher, View.OnKeyListener {

    private var blockEvents = false

    override fun onKey(view: View, keyCode: Int, event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) {
            return true
        }

        if (event.keyCode == KeyEvent.KEYCODE_DEL) {
            codeEditText.changeFocusedDigit(digitEditTextIndex - 1)
        } else if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
            codeEditText.notifyThatCodeEntered()
        }

        return false
    }

    override fun afterTextChanged(source: Editable) {
        if (source.isEmpty() || blockEvents) {
            return
        }

        if (codeEditText.isPositioningEnabled && filterSource(source)) {
            if (digitEditTextIndex != codeEditText.digitEditTexts!!.lastIndex) {
                codeEditText.changeFocusedDigit(digitEditTextIndex + 1)
            } else {
                codeEditText.notifyThatCodeEntered()
            }
        }
    }

    private fun filterSource(source: Editable): Boolean {
        if (source.length > 1) {
            val selection = digitEditText.selectionStart - source.length

            blockEvents = true

            if (selection >= 0) {
                source.delete(0, source.length - 1)
            } else {
                source.delete(1, source.length)
            }

            blockEvents = false
        }

        if (source.isNotEmpty() && !source[0].isDigit()) {
            blockEvents = true
            source.delete(0, 1)
            blockEvents = false
        }

        return source.isNotEmpty()
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
}