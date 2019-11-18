package com.sudox.design

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.showSoftKeyboard() {
    if (requestFocus()) {
        (context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}