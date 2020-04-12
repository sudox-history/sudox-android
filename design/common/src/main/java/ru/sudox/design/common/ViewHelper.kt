package ru.sudox.design.common

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlin.math.min

fun View.lazyLayout(newLeft: Int, newTop: Int, newRight: Int, newBottom: Int) {
    if (newLeft != left || newTop != top || newRight != right || newBottom != bottom) {
        layout(newLeft, newTop, newRight, newBottom)
    }
}

/**
 * Расчитывает значение одного из размерных параметров View.
 *
 * @param spec Спецификация к размеру View.
 * @param need Минимальный возможный размер View.
 * @return Нужный размер View.
 */
@Suppress("unused")
fun calculateViewSize(spec: Int, need: Int): Int {
    val mode = View.MeasureSpec.getMode(spec)
    val available = View.MeasureSpec.getSize(spec)

    return if (mode == View.MeasureSpec.EXACTLY) {
        available
    } else if (mode == View.MeasureSpec.AT_MOST) {
        min(need, available)
    } else {
        need
    }
}

fun View.showSoftKeyboard() {
    if (requestFocus()) {
        (context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun Activity.hideSoftKeyboard() {
    if (currentFocus != null) {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}