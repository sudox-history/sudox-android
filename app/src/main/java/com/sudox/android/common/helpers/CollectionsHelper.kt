package com.sudox.android.common.helpers

fun <T> ArrayList<T>.findAndRemoveIf(callback: (T) -> (Boolean)) {
    for (element in this) {
        if (callback(element)) {
            minusAssign(element)
            break
        }
    }
}