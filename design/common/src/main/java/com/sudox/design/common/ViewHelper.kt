package com.sudox.design.common

import android.view.View
import kotlin.math.min

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