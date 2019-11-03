package com.sudox.design.nicknameEditText

import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher

class NicknameTextWatcher(val editText: NicknameEditText) : TextWatcher {

    private var splitterIndexBeforeChange = -1
    private var changesIgnored = false

    override fun afterTextChanged(s: Editable) {
        var splitterIndex = s.indexOfLast { it == TAG_SPLITTER }

        if (changesIgnored || editText.tag == null) {
            return
        }

        changesIgnored = true

        if (splitterIndexBeforeChange != -1 && splitterIndexBeforeChange < s.length && splitterIndex == -1) {
            s.delete(splitterIndexBeforeChange, s.length)
        }

        if (splitterIndex != -1 && s.subSequence(splitterIndex, s.length).toString() != editText.tag) {
            s.delete(splitterIndex, s.length)
            splitterIndex = -1
        }

        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd

        if (splitterIndex == -1) {
            val tagStartIndex = s.length
            val tagEndIndex = s.length + editText.tag!!.length

            s.insert(tagStartIndex, editText.tag)
            s.setSpan(editText.tagColorSpannable, tagStartIndex, tagEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            splitterIndex = tagStartIndex
        }

        if (splitterIndex == 0) {
            s.delete(splitterIndex, s.length)
        }

        if (selectionStart == splitterIndex) {
            editText.scrollToEnd()
        }

        editText.setSelection(selectionStart, selectionEnd)
        changesIgnored = false
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if (changesIgnored || editText.tag == null) {
            return
        }

        splitterIndexBeforeChange = s.indexOfLast { it == TAG_SPLITTER }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
}